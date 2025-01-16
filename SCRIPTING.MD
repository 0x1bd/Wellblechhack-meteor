# Scripting

Wellblechhack provides a way of creating custom scripts using kotlin-scripting.
Use the ingame "Scripts" tab to create, run and edit scripts.
The WBH standard library (stdLib) provides a set of utility functions. All functions need to be prefixed with `wbh.`.

The `ScriptingStandardLibrary` provides a set of utility functions for interacting with Minecraft and logging messages in Meteor Client scripts. This library is automatically available in scripts, and its functions can be called directly.

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
// Log a message
wbh.info("Starting script...")

// Send a chat message
wbh.chat("Hello, Minecraft!")

// Check if a diamond is in slot 0 of the player inventory
if (wbh.isItemInSlot(0, "Diamond", "inventory")) {
    wbh.feedback("Diamond found in slot 0!")
} else {
    wbh.feedback("No diamond in slot 0.")
}

// Move an item from slot 0 to slot 1 in the player inventory
wbh.moveItem(0, 1, "inventory")

// Pause for 2 seconds
wbh.delay(2000)

// Log the open container type
wbh.feedback("Open container: ${wbh.getOpenContainer()}")

// Display feedback in-game
wbh.feedback("Script executed successfully!")
