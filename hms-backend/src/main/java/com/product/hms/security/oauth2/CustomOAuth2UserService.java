package com.product.hms.security.oauth2;

import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.UserEntity;
import com.product.hms.enums.Role;
import com.product.hms.repository.CustomerRepository;
import com.product.hms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    @Transactional
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String provider = registrationId.toUpperCase();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = extractEmail(attributes, provider);
        String name = extractName(attributes, provider);

        if (email == null) {
            throw new InternalAuthenticationServiceException("Email not found from OAuth2 provider");
        }

        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        UserEntity user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!provider.equals(user.getProvider())) {
                throw new InternalAuthenticationServiceException(
                        "Email already registered with " + user.getProvider() + " provider");
            }
            user = updateExistingUser(user, attributes, provider);
        } else {
            user = registerNewUser(attributes, provider, email, name);
        }

        return OAuth2UserPrincipal.create(user, attributes, provider);
    }

    private String extractEmail(Map<String, Object> attributes, String provider) {
        switch (provider) {
            case "GOOGLE":
                return (String) attributes.get("email");
            default:
                return null;
        }
    }

    private String extractName(Map<String, Object> attributes, String provider) {
        switch (provider) {
            case "GOOGLE":
                return (String) attributes.get("name");
            default:
                return null;
        }
    }

    @Transactional
    private UserEntity registerNewUser(Map<String, Object> attributes, String provider, String email, String name) {
        // Tạo User
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setProvider(provider);
        user.setProviderId((String) attributes.get("sub"));
        user.setRole(Role.CUSTOMER.name());
        user.setIsActive(true);

        UserEntity savedUser = userRepository.save(user);

        // Tạo Customer tương ứng
        CustomerEntity customer = new CustomerEntity();
        customer.setUserEntity(savedUser);
        customer.setEmail(savedUser.getEmail());
        customer.setFullName(name != null ? name : email.split("@")[0]); // Nếu không có name thì lấy phần trước @
        customer.setType("REGULAR");
        customer.setIsActive(true);
        customerRepository.save(customer);

        // Set customer cho user (quan hệ 2 chiều)
        savedUser.setCustomerEntity(customer);

        return userRepository.save(savedUser);
    }

    @Transactional
    private UserEntity updateExistingUser(UserEntity existingUser, Map<String, Object> attributes, String provider) {
        existingUser.setProvider(provider);
        existingUser.setProviderId((String) attributes.get("sub"));

        // Cập nhật customer nếu cần
        if (existingUser.getCustomerEntity() != null) {
            CustomerEntity customer = existingUser.getCustomerEntity();
            String name = extractName(attributes, provider);
            if (name != null && !name.isEmpty()) {
                customer.setFullName(name);
                customerRepository.save(customer);
            }
        }

        return userRepository.save(existingUser);
    }
}
