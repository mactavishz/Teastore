import { createContext } from 'react';
import type { GlobalStateContextType } from "~/types";

// Provide a default value that matches the shape of GlobalStateContextType
const defaultState: GlobalStateContextType = {
  categoryList: [],
  message: null,
  errorMessage: null,
  setMessage: () => {},
  setErrorMessage: () => {},
  isLoggedIn: false,
  baseURL: "/",
};

// Create the context with the correct type
export const GlobalStateContext = createContext<GlobalStateContextType>(defaultState);
