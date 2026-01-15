# WRP Bot — Discord Bot Project

---

### 📌 Project Overview

**WRP Bot** is a Discord bot developed for the *WRP Discord server* (currently inactive).
The project was created collaboratively and served as a practical implementation of a multi-functional Discord bot with moderation, economy, and inventory systems.

This repository is maintained as a **portfolio and academic project**, demonstrating software design, Java development skills, and real-world API integration.

---

### 👨‍💻 Project Purpose

The main goal of this project was to:

* Gain hands-on experience with Java backend development
* Learn how to work with third-party APIs (Discord / JDA)
* Design a modular command-based architecture
* Implement persistent user data storage
* Develop an in-server economy and shop system

---

### ⚙️ Technical Stack

* **Language:** Java
* **Build Tool:** Gradle
* **API:** JDA (Java Discord API)
* **Logging:** SLF4J
* **Platform:** Discord
* **Architecture:** Command-based, modular design

---

### 🧠 Architecture Overview

The bot is built around a centralized entry point (`Main.java`) and uses a **command registry pattern** to manage all available commands.

Key architectural elements:

* Command registry for scalable command management
* Separate admin and user command layers
* Persistent user data management
* Shop manager for economy items
* Graceful shutdown handling with auto-save

---

### 🔐 Admin Commands

These commands are restricted to server administrators and moderators:

* **BanCommand** — bans a user from the server
* **KickCommand** — removes a user from the server
* **MuteCommand** — restricts a user from sending messages
* **AddItemCommand** — adds an item to a user's inventory
* **RemoveItemCommand** — removes an item from a user's inventory
* **GiveMoneyCommand** — adds currency to a user's balance
* **RemoveMoneyCommand** — removes currency from a user's balance

---

### 👤 User Commands

Commands available to all server members:

* **BalanceCommand** — shows the user's current balance
* **ViewBalanceCommand** — displays another user's balance
* **BallCommand** — shows detailed information about a server member
* **InventoryCommand** — displays the user's inventory
* **PayCommand** — transfers money between users
* **WorkCommand** — grants **2000 coins once every 24 hours**
* **BuyCommand** — allows purchasing items or structures
* **CollectCommand** — collects profit from owned structures
* **ShopCommand** — displays available shop items
* **RulesCommand** — shows server rules
* **InfoCommand** — sends an embedded information message about the bot
* **PingCommand** — displays the bot’s current latency

---

### 🛒 Shop & Economy System

The bot includes an internal economy system with currency, inventories, and structures.

#### ShopManager.java

`ShopManager` is responsible for:

* Storing the list of shop items
* Managing prices
* Providing item lookup logic
* Acting as a centralized shop registry

This approach allows the shop to be extended without modifying command logic.

---

### 💾 Data Persistence

User-related data includes:

* Balance
* Inventory
* Owned structures

All data is managed by `UserDataManager` and is automatically saved when the application shuts down using a JVM shutdown hook.

This ensures data integrity even during unexpected termination.

---

### 🚀 Application Startup

During startup:

1. The bot token is loaded from configuration
2. JDA is initialized with required gateway intents
3. Event listeners are registered
4. Commands are registered
5. Bot presence is set
6. The bot waits until it is fully ready before operating

---

### 📦 Project Status

**Archived / Inactive**
The original Discord server (WRP) is no longer available.
This repository remains as a demonstration of Java development, architecture, and API integration skills.

---

### 🎓 Academic & Portfolio Use

This project is suitable for:

* University portfolio submissions
* Demonstrating Java and backend fundamentals
* Showcasing API integration experience
* Highlighting real-world project structure

---

### 👥 Contributors

* **Semen** — core development, architecture, economy system
* **Co-developer** — collaborative development
