import {Route} from "react-router-dom";
import {StaffLayout} from "../../layouts/staff/StaffLayout.jsx";
import {NAV_ITEMS} from "../../constants/staff.jsx";

export const StaffRoutes = (
        <Route path={"/staff"} element={<StaffLayout/>}>
            <Route index element={<div>Staff Dashboard</div>}/>
            {
                NAV_ITEMS.map(item => {
                    return (
                            /**
                             * @type {React.JSX.Element}
                             */
                            <Route key={item.to} path={item.to.replace("/staff/", "")} element={item.element}/>
                    )
                })
            }
        </Route>
);
