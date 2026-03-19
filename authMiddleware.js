const jwt = require("jsonwebtoken");

const APP_SECRET = "myappsecret";
const TOKEN_PREFIX = "Bearer<";
const TOKEN_SUFFIX = ">";

function sanitizeUser(user) {
  if (!user) {
    return null;
  }

  const { password, ...safeUser } = user;
  return safeUser;
}

function createToken(user) {
  return jwt.sign(
    {
      userId: user.id,
      username: user.username,
      role: user.role || "user"
    },
    APP_SECRET,
    { expiresIn: "8h" }
  );
}

function extractToken(headerValue) {
  const header = headerValue || "";
  if (header.startsWith(TOKEN_PREFIX) && header.endsWith(TOKEN_SUFFIX)) {
    return header.substring(TOKEN_PREFIX.length, header.length - TOKEN_SUFFIX.length);
  }
  return "";
}

function isAdmin(payload) {
  return payload?.role === "admin";
}

function getOrderOwner(db, orderId) {
  return db.get("orders").getById(orderId).value();
}

function getUser(db, userId) {
  return db.get("users").getById(userId).value();
}

function deny(res, message = "Unauthorized", statusCode = 401) {
  res.status(statusCode).json({ success: false, message });
}

module.exports = function (req, res, next) {
  const db = req.app.db;
  const url = req.path || req.url;

  if (url === "/login" && req.method === "POST") {
    const username = req.body?.username || req.body?.name;
    const password = req.body?.password;
    const user = db.get("users")
      .find({ username, password })
      .value();

    if (!user) {
      res.json({ success: false, message: "Invalid username or password" });
      return;
    }

    res.json({
      success: true,
      token: createToken(user),
      user: sanitizeUser(user)
    });
    return;
  }

  if (url === "/register" && req.method === "POST") {
    const { nom, prenom, adresse, telephone, username, password } = req.body || {};

    if (!nom || !prenom || !adresse || !telephone || !username || !password) {
      deny(res, "Missing required fields", 400);
      return;
    }

    const existingUser = db.get("users").find({ username }).value();
    if (existingUser) {
      deny(res, "Username already exists", 400);
      return;
    }

    const nextId = db.get("users").value().reduce((max, user) => Math.max(max, user.id || 0), 0) + 1;
    const newUser = {
      id: nextId,
      role: "user",
      nom,
      prenom,
      adresse,
      telephone,
      username,
      password,
      cart: {
        lines: [],
        itemCount: 0,
        cartPrice: 0
      }
    };

    db.get("users").insert(newUser).write();

    res.status(201).json({
      success: true,
      token: createToken(newUser),
      user: sanitizeUser(newUser)
    });
    return;
  }

  const protectedRoute =
    url.startsWith("/orders") ||
    url.startsWith("/users") ||
    (url.startsWith("/products") && req.method !== "GET");

  if (!protectedRoute) {
    next();
    return;
  }

  const token = extractToken(req.headers["authorization"]);

  try {
    req.auth = jwt.verify(token, APP_SECRET);
  } catch (err) {
    deny(res);
    return;
  }

  if (url.startsWith("/products") && req.method !== "GET" && !isAdmin(req.auth)) {
    deny(res, "Admin access required", 403);
    return;
  }

  if (url === "/orders" && req.method === "GET" && !isAdmin(req.auth)) {
    req.query.userId = String(req.auth.userId);
  }

  if (url === "/orders" && req.method === "POST") {
    const currentUser = getUser(db, req.auth.userId);
    req.body.userId = req.auth.userId;
    req.body.username = currentUser?.username || req.auth.username;
  }

  if (url.match(/^\/orders\/\d+$/)) {
    const orderId = Number(url.split("/").pop());
    const order = getOrderOwner(db, orderId);

    if (!order) {
      deny(res, "Order not found", 404);
      return;
    }

    if (!isAdmin(req.auth) && order.userId !== req.auth.userId) {
      deny(res, "Access denied", 403);
      return;
    }
  }

  if (url === "/users" && req.method === "GET" && !isAdmin(req.auth)) {
    deny(res, "Admin access required", 403);
    return;
  }

  if (url.match(/^\/users\/\d+$/)) {
    const userId = Number(url.split("/").pop());
    const user = getUser(db, userId);

    if (!user) {
      deny(res, "User not found", 404);
      return;
    }

    if (!isAdmin(req.auth) && req.auth.userId !== userId) {
      deny(res, "Access denied", 403);
      return;
    }

    if (!isAdmin(req.auth) && req.method === "PATCH") {
      const nextUsername = req.body.username;
      if (nextUsername && nextUsername !== user.username) {
        const existingUser = db.get("users").find({ username: nextUsername }).value();
        if (existingUser && existingUser.id !== user.id) {
          deny(res, "Username already exists", 400);
          return;
        }
      }

      req.body = {
        ...req.body,
        role: user.role,
        cart: req.body.cart ?? user.cart
      };

      if (!req.body.password) {
        req.body.password = user.password;
      }
    }
  }

  next();
};
