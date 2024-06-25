export interface IconData {
  icon: string
}

export interface Category {
  id: number;
  name: string;
  description: string;
}

export interface Product {
  id: number;
  categoryId: number;
  name: string;
  description: string;
  listPriceInCents: number;
  image?: string;
}

export interface GlobalStateContextType {
  categoryList: Category[];
  setCategoryList: React.Dispatch<React.SetStateAction<Category[]>>;
}