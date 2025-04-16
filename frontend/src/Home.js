import React from "react";
import axiosInstance from "./helper/axios";

import { Avatar, List, ListItem, ListItemIcon, ListItemText } from "@mui/material";
import OwnerItem from "./OwnerItem";

class Home extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            owners: [],
        };
    }

    componentDidMount() {
        axiosInstance
            .get("/users?role=OWNER")  // <--- adaptat pentru filtrare după rol
            .then((res) => {
                this.setState({ owners: res.data });
            })
            .catch((error) => {
                console.log("Eroare la fetch:", error);
            });
    }

    render() {
        return (
            <React.Fragment>
                <List key={"owners"}>
                    {this.state.owners.map((owner) => (
                        <div key={owner.id}>
                            <OwnerItem owner={owner} /> {/* sau adaptează aici */}
                        </div>
                    ))}
                </List>
            </React.Fragment>
        );
    }
}

export default Home;
