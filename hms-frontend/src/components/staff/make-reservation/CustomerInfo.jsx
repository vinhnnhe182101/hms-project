import {Button, Grid, Group, Stack, Textarea, TextInput} from "@mantine/core";
import {customerApi} from "../../../apis/staff/customerApi";
import {useMakeReservationArea} from "../../../hooks/common/area/make-reservation-area-provider";
import {useObjectState} from "../../../hooks/common/use-object-state";
import {SectionCard} from "../../common/SectionCard";
import {IconSearch} from "@tabler/icons-react";

export const CustomerInfo = () => {
    const {state: reservationRequest, setState: setReservationRequest} = useMakeReservationArea();
    const {
        data: customerRequest,
        updateField,
        setData: setCustomerRequest,
    } = useObjectState(reservationRequest.customerRequest);
    const {updateField: updateReservationRequest} = useObjectState(reservationRequest);

    /**
     * @param {keyof CustomerRequest} field - Tên trường cần cập nhật trong customerRequest
     * @param {string} value - Giá trị mới để cập nhật cho trường đó
     */
    const updateCustomerRequest = (field, value) => {
        updateField(field, value);
        setReservationRequest((prev) => ({
            ...prev,
            customerRequest: customerRequest,
        }));
    };

    const searchCustomerHandler = async () => {
        if (!customerRequest.identityCard) {
            alert("Please enter an ID/Passport number to search.");
            return;
        }

        const customerResponse = await customerApi.getCustomerByIdentityCard(customerRequest.identityCard);
        if (customerResponse) {
            updateCustomerRequest("customerId", customerResponse.id);
            updateCustomerRequest("fullName", customerResponse.fullName);
            updateCustomerRequest("phoneNumber", customerResponse.phoneNumber);
            updateCustomerRequest("email", customerResponse.email);
        } else {
            alert("Customer not found. Please check the ID/Passport number and try again.");
        }

        console.log("Search Customer Response:", customerResponse);
        console.log("ReservationRequest: ", reservationRequest);
    };

    return (
            <SectionCard title="3. Customer Information">
                <Grid gutter="md">
                    <Grid.Col span={{base: 12, sm: 7}}>
                        <Stack gap="sm">
                            <Group gap="xs" align="flex-end">
                                <TextInput
                                        label="ID / Passport Number"
                                        placeholder="Enter to search..."
                                        value={customerRequest.identityCard}
                                        onChange={(e) => updateCustomerRequest("identityCard", e.target.value)}
                                        onKeyDown={(e) => e.key === "Enter" && searchCustomerHandler()}
                                        style={{flex: 1}}
                                        radius="md"
                                />
                                <Button
                                        variant="light"
                                        onClick={searchCustomerHandler}
                                        leftSection={<IconSearch size={16}/>}
                                >
                                    Search
                                </Button>
                            </Group>
                            <TextInput label="Full Name" value={customerRequest?.fullName ?? ""} radius="md"/>
                            <Grid>
                                <Grid.Col span={6}>
                                    <TextInput
                                            label="Phone Number"
                                            value={customerRequest?.phoneNumber ?? ""}
                                            radius="md"
                                    />
                                </Grid.Col>
                                <Grid.Col span={6}>
                                    <TextInput label="Email" value={customerRequest?.email ?? ""} radius="md"/>
                                </Grid.Col>
                            </Grid>
                        </Stack>
                    </Grid.Col>
                    <Grid.Col span={{base: 12, sm: 5}}>
                        <Textarea
                                label="Order Note"
                                placeholder="Special requests (room preference, transfer, etc.)"
                                value={reservationRequest.note}
                                onChange={(e) => updateReservationRequest("note", e.target.value)}
                                minRows={7}
                                radius="md"
                        />
                    </Grid.Col>
                </Grid>
            </SectionCard>
    );
};
