import {Route} from "react-router-dom";
import {StaffLayout} from "../../layouts/staff/StaffLayout.jsx";
import {STAFF_ROUTES} from "../../constants/staff.jsx";

export const StaffRoutes = (
        <Route path={"/staff"} element={<StaffLayout/>}>
            <Route index element={<div>Staff Dashboard</div>}/>
            {
                STAFF_ROUTES.map(item => {
                    return (
                            /**
                             * @type {React.JSX.Element}
                             */
                            <Route key={item.path} path={item.path.replace("/staff/", "")} element={item.element}/>
                    )
                })
            }
        </Route>
);
