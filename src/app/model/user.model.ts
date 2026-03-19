import { StoredCart } from "./cart.model";

export interface User {
  id?: number;
  role?: "admin" | "user";
  nom?: string;
  prenom?: string;
  adresse?: string;
  telephone?: string;
  username?: string;
  password?: string;
  cart?: StoredCart;
}

export interface AuthResponse {
  success: boolean;
  token?: string;
  user?: User;
  message?: string;
}

export interface AuthSession {
  token: string;
  user: User;
}
