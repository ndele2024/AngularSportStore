module.exports = function () {
  return {
    products: [
      {
        id: 1,
        name: "Kayak",
        category: "Watersports",
        description: "A boat for one person",
        price: 275,
        imageUrl: "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80"
      },
      {
        id: 2,
        name: "Lifejacket",
        category: "Watersports",
        description: "Protective and fashionable",
        price: 48.95,
        imageUrl: "https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=900&q=80"
      },
      {
        id: 3,
        name: "Soccer Ball",
        category: "Soccer",
        description: "FIFA-approved size and weight",
        price: 19.5,
        imageUrl: "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?auto=format&fit=crop&w=900&q=80"
      },
      {
        id: 4,
        name: "Corner Flags",
        category: "Soccer",
        description: "Give your playing field a professional touch",
        price: 34.95,
        imageUrl: "https://images.unsplash.com/photo-1547347298-4074fc3086f0?auto=format&fit=crop&w=900&q=80"
      },
      {
        id: 5,
        name: "Stadium",
        category: "Soccer",
        description: "Flat-packed 35,000-seat stadium",
        price: 79500,
        imageUrl: "https://images.unsplash.com/photo-1508098682722-e99c643e7485?auto=format&fit=crop&w=900&q=80"
      },
      {
        id: 6,
        name: "Thinking Cap",
        category: "Chess",
        description: "Improve brain efficiency by 75%",
        price: 16,
        imageUrl: "https://images.unsplash.com/photo-1528819622765-d6bcf132f793?auto=format&fit=crop&w=900&q=80"
      },
      {
        id: 7,
        name: "Unsteady Chair",
        category: "Chess",
        description: "Secretly give your opponent a disadvantage",
        price: 29.95,
        imageUrl: "https://images.unsplash.com/photo-1505842465776-3ac3b43d5d39?auto=format&fit=crop&w=900&q=80"
      },
      {
        id: 8,
        name: "Human Chess Board",
        category: "Chess",
        description: "A fun game for the family",
        price: 75,
        imageUrl: "https://images.unsplash.com/photo-1586165368502-1bad197a6461?auto=format&fit=crop&w=900&q=80"
      },
      {
        id: 9,
        name: "Bling King",
        category: "Chess",
        description: "Gold-plated, diamond-studded King",
        price: 1200,
        imageUrl: "https://images.unsplash.com/photo-1611195974226-4c4f3cc8e3d6?auto=format&fit=crop&w=900&q=80"
      }
    ],
    users: [
      {
        id: 1,
        role: "admin",
        nom: "Admin",
        prenom: "Super",
        adresse: "1 Administration Way",
        telephone: "555-000-0000",
        username: "admin",
        password: "secret",
        cart: {
          lines: [],
          itemCount: 0,
          cartPrice: 0
        }
      },
      {
        id: 2,
        role: "user",
        nom: "Doe",
        prenom: "Jane",
        adresse: "25 Market Street",
        telephone: "555-111-2233",
        username: "jane",
        password: "password",
        cart: {
          lines: [],
          itemCount: 0,
          cartPrice: 0
        }
      }
    ],
    orders: []
  };
};
