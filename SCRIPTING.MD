# Scripting

Wellblechhack provides a way of creating custom scripts using kotlin-scripting.
Use the ingame "Scripts" tab to create, run and edit scripts.
The WBH standard library (stdLib) provides a set of utility functions. All functions need to be prefixed with `wbh.`.

## Available Functions

### Logging
- **`info(message: String)`**
  Logs an informational message to the console.
  Example: `wbh.info("Hello, World!")`

- **`warn(message: String)`**
  Logs a warning message to the console.
  Example: `wbh.warn("This is a warning!")`

- **`error(message: String)`**
  Logs an error message to the console.
  Example: `wbh.error("Something went wrong!")`

### In-Game Feedback
- **`feedback(message: String)`**
  Displays a message in the in-game chat HUD.
  Example: `wbh.feedback("Script executed successfully!")`

### Commands and Chat
- **`command(command: String)`**
  Executes a Minecraft command.
  Example: `wbh.command("/gamemode creative")`

- **`chat(message: String)`**
  Sends a chat message.
  Example: `wbh.chat("Hello, Minecraft!")`

### Inventory Management
- **`isItemInSlot(slot: Int, item: String, where: String): Boolean`**
  Checks if a specific item is in the given slot. The `where` parameter can be `"inventory"` (player inventory) or `"container"` (open container).
  Example: `if (wbh.isItemInSlot(0, "Diamond", "inventory")) { wbh.info("Diamond found in slot 0!") }`

- **`getOpenContainer(): String`**
  Returns the type of the currently open container.
  Example: `wbh.info("Open container: ${wbh.getOpenContainer()}")`

- **`clickSlot(slot: Int, button: Int, where: String)`**
  Clicks a slot in the specified location (`"inventory"` or `"container"`).
  Example: `wbh.clickSlot(0, 0, "container")` (Left-clicks the first slot in the container)

- **`moveItem(sourceSlot: Int, destinationSlot: Int, where: String)`**
  Moves an item from one slot to another in the specified location (`"inventory"` or `"container"`).
  Example: `wbh.moveItem(0, 1, "inventory")` (Moves item from slot 0 to slot 1 in the player inventory)

- **`moveItem(sourceSlot: Int, destinationSlot: Int, where: String, simulate: Boolean)`**
  Moves an item from one slot to another, optionally simulating the action.
  Example: `wbh.moveItem(0, 1, "inventory", true)` (Simulates moving the item in the player inventory)

- **`dropStack(slot: Int, where: String)`**
  Drops the stack in the specified slot (`"inventory"` or `"container"`).
  Example: `wbh.dropStack(0, "inventory")` (Drops the item in slot 0 of the player inventory)

- **`getItems(where: String): List<ItemStack>`**
  Returns a list of items in the specified location (`"inventory"` or `"container"`).
  Example: `wbh.getItems("inventory").forEach { wbh.info("Item: ${wbh.getItemName(it)}") }`

### Item Data
- **`getItemName(stack: ItemStack): String`**
  Returns the name of the item stack.
  Example: `wbh.info("Item name: ${wbh.getItemName(stack)}")`

- **`getItemCount(stack: ItemStack): Int`**
  Returns the count of items in the stack.
  Example: `wbh.info("Item count: ${wbh.getItemCount(stack)}")`

- **`getMaxItemCount(stack: ItemStack): Int`**
  Returns the maximum stack size for the item.
  Example: `wbh.info("Max stack size: ${wbh.getMaxItemCount(stack)}")`

- **`getItemDamage(stack: ItemStack): Int`**
  Returns the damage value of the item.
  Example: `wbh.info("Item damage: ${wbh.getItemDamage(stack)}")`

- **`getItemMaxDamage(stack: ItemStack): Int`**
  Returns the maximum damage value of the item.
  Example: `wbh.info("Max item damage: ${wbh.getItemMaxDamage(stack)}")`

- **`getItem(slot: Int, where: String): ItemStack`**
  Returns the item in the specified slot (`"inventory"` or `"container"`).
  Example: `val item = wbh.getItem(0, "inventory")`

### Utilities
- **`delay(duration: Long)`**
  Pauses the script for the specified duration (in milliseconds).
  Example: `wbh.delay(1000)` (Pauses for 1 second)

## Example Script

```kotlin
// Log the start of the script
wbh.info("Starting script...")

// Send a chat message to the server
wbh.chat("Hello, Minecraft!")

// Check if a Diamond is in slot 0 of the player's inventory
if (wbh.isItemInSlot(0, "Diamond", "inventory")) {
    wbh.feedback("Diamond found in player inventory!")
} else {
    wbh.feedback("No diamond in player inventory.")
}

// Check if Sand is in slot 1 of the player's inventory
if (wbh.isItemInSlot(1, "Sand", "inventory")) {
    wbh.feedback("Sand found in player inventory!")
} else {
    wbh.feedback("No sand in player inventory.")
}

// Get the type of the currently open container
val containerType = wbh.getOpenContainer()
wbh.feedback("Open container: $containerType")

// Get the item in slot 0 of the open container
val containerItem = wbh.getItem(0, "container")
wbh.feedback("Container item in slot 0: ${wbh.getItemName(containerItem)} (Count: ${wbh.getItemCount(containerItem)})")

// Get the item in slot 9 of the player's inventory (first row, last slot)
val playerItem = wbh.getItem(9, "inventory")
wbh.feedback("Player item in slot 9: ${wbh.getItemName(playerItem)} (Count: ${wbh.getItemCount(playerItem)})")

// Get the item in the player's helmet slot (slot 39)
val helmetItem = wbh.getItem(39, "inventory")
wbh.feedback("Player helmet: ${wbh.getItemName(helmetItem)} (Damage: ${wbh.getItemDamage(helmetItem)}/${wbh.getItemMaxDamage(helmetItem)})")

// Get the item in the player's chestplate slot (slot 38)
val chestplateItem = wbh.getItem(38, "inventory")
wbh.feedback("Player chestplate: ${wbh.getItemName(chestplateItem)} (Damage: ${wbh.getItemDamage(chestplateItem)}/${wbh.getItemMaxDamage(chestplateItem)})")

// Get the item in the player's offhand slot (slot 40)
val offhandItem = wbh.getItem(40, "inventory")
wbh.feedback("Player offhand: ${wbh.getItemName(offhandItem)} (Count: ${wbh.getItemCount(offhandItem)})")

// Click slot 0 in the open container with left mouse button (button 0)
wbh.clickSlot(0, 0, "container")
```
