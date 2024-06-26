import { createContext, useContext, useState, ReactNode } from 'react';
import type { GlobalStateContextType, Category } from "~/types";

// Provide a default value that matches the shape of GlobalStateContextType
const defaultState: GlobalStateContextType = {
  categoryList: [],
  message: null,
  errorMessage: null,
  setMessage: () => {},
  setErrorMessage: () => {},
};

// Create the context with the correct type
export const GlobalStateContext = createContext<GlobalStateContextType>(defaultState);
