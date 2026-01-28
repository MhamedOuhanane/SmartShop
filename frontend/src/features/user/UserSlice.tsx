import type {User, UserState} from "./UserType.tsx";
import { createSlice, type PayloadAction } from "@reduxjs/toolkit"

const initialState: UserState = {
    user: null,
    isAuthenticated: false
}

const userSlice  = createSlice({
    name: "user",
    initialState,
    reducers: {
        loginSuccess: (state: UserState, action: PayloadAction<User>)=> {
            state.user = action.payload;
            state.isAuthenticated = true;
        },

        logout: (state: UserState) => {
            state.user = null;
            state.isAuthenticated = false;
        }
    }
});

export const { loginSuccess, logout } = userSlice.actions;
export default userSlice.reducer;