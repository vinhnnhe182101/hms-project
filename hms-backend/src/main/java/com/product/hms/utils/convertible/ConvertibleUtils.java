package com.product.hms.utils.convertible;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.hms.utils.convertible.exceptions.ErrorMappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * Lớp cơ sở generic dùng để chuyển đổi giữa Entity, Request và Response bằng ModelMapper.
 *
 * @param <Entity>   Kiểu thực thể (Entity).
 * @param <Request>  Kiểu dữ liệu nhận từ phía client (Request).
 * @param <Response> Kiểu dữ liệu trả về phía client (Response).
 */
public class ConvertibleUtils<Entity, Request, Response> {
    private final Class<Entity> entityClass;
    private final Class<Request> requestClass;
    private final Class<Response> responseClass;
    private ModelMapper modelMapper;

    /**
     * Khởi tạo ConverterBase với thông tin các class cần thiết.
     *
     * @param entityClass   Class của Entity.
     * @param requestClass  Class của Request.
     * @param responseClass Class của Response.
     */
    public ConvertibleUtils(Class<Entity> entityClass, Class<Request> requestClass, Class<Response> responseClass) {
        this.entityClass = entityClass;
        this.requestClass = requestClass;
        this.responseClass = responseClass;
    }

    /**
     * Setter cho ModelMapper được quản lý bởi Spring.
     *
     * @param modelMapper Instance ModelMapper được inject tự động.
     */
    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Chuyển đổi một đối tượng từ kiểu nguồn sang kiểu đích bằng ModelMapper.
     *
     * @param source           Đối tượng nguồn.
     * @param destinationClass Class của kiểu đích.
     * @param <Source>         Kiểu nguồn.
     * @param <Destination>    Kiểu đích.
     * @return Đối tượng đã chuyển đổi sang kiểu đích.
     * @throws ErrorMappingException Nếu chuyển đổi thất bại.
     */
    protected <Source, Destination> Destination map(Source source, Class<Destination> destinationClass) {
        if (source == null) {
            return null;
        }
        Destination result = modelMapper.map(source, destinationClass);
        return Optional.ofNullable(result)
                .orElseThrow(() -> new ErrorMappingException(source.getClass(), destinationClass));
    }

    /**
     * Chuyển đổi từ Request sang Entity.
     *
     * @param request Đối tượng request.
     * @return Đối tượng entity tương ứng.
     */
    public Entity toEntity(Request request) {
        return map(request, entityClass);
    }

    /**
     * Chuyển đổi danh sách Request sang danh sách Entity.
     *
     * @param requests Danh sách đối tượng request.
     * @return Danh sách đối tượng entity tương ứng.
     */
    public List<Entity> toEntities(List<Request> requests) {
        return requests.stream().map(this::toEntity).toList();
    }

    /**
     * Chuyển đổi từ Entity sang Response.
     *
     * @param entity Đối tượng entity.
     * @return Đối tượng response tương ứng.
     */
    public Response toResponse(Entity entity) {
        return map(entity, responseClass);
    }

    /**
     * Chuyển đổi danh sách Entity sang danh sách Response.
     *
     * @param entities Danh sách đối tượng entity.
     * @return Danh sách đối tượng response tương ứng.
     */
    public List<Response> toResponses(List<Entity> entities) {
        return entities.stream().map(this::toResponse).toList();
    }

    public Request fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(json, requestClass);
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse json: " + e.getMessage());
        }
    }
}
