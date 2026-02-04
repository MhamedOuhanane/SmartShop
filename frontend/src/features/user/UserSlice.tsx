import type {User, UserState} from "../../type/UserType.tsx";
import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

const savedUser = localStorage.getItem("user");
const parsedUser: User | null = savedUser ? JSON.parse(savedUser) : null;

const initialState: UserState = {
    user: parsedUser,
    isAuthenticated: !!parsedUser,
};

const userSlice  = createSlice({
    name: "user",
    initialState,
    reducers: {
        loginSuccess: (state: UserState, action: PayloadAction<User>)=> {
            state.user = action.payload;
            state.isAuthenticated = true;
            
            localStorage.setItem("user", JSON.stringify(action.payload));
        },

        logout: (state: UserState) => {
            state.user = null;
            state.isAuthenticated = false;
            
            localStorage.removeItem("user");
        }
    }
});

export const { loginSuccess, logout } = userSlice.actions;
export default userSlice.reducer;