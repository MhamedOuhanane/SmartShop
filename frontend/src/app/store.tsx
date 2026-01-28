import { configureStore } from '@reduxjs/toolkit'
import UserReducer from "../features/user/UserSlice.tsx"

export const store = configureStore({
    reducer: {
        user: UserReducer
    },
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch