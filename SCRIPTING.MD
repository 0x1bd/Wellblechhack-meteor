# Scripting

Wellblechhack provides a way of creating custom scripts using kotlin-scripting.
Use the ingame "Scripts" tab to create, run and edit scripts.
The WBH standard library (stdLib) provides a set of utility functions. All functions need to be prefixed with `wbh.`.

The `ScriptingStandardLibrary` provides a set of utility functions for interacting with Minecraft and logging messages in Meteor Client scripts. This library is automatically available in scripts, and its functions can be called directly.

## Available Functions

### Logging
- **`info(message: String)`**
  Logs an informational message to the console.
  Example: `info("Hello, World!")`

- **`warn(message: String)`**
  Logs a warning message to the console.
  Example: `warn("This is a warning!")`

- **`error(message: String)`**
  Logs an error message to the console.
  Example: `error("Something went wrong!")`

### In-Game Feedback
- **`feedback(message: String)`**
  Displays a message in the in-game chat HUD.
  Example: `feedback("Script executed successfully!")`

### Commands and Chat
- **`command(command: String)`**
  Executes a Minecraft command.
  Example: `command("/gamemode creative")`

- **`chat(message: String)`**
  Sends a chat message.
  Example: `chat("Hello, Minecraft!")`

### Inventory Management
- **`isItemInSlot(slot: Int, item: String): Boolean`**
  Checks if a specific item is in the given inventory slot.
  Example: `if (isItemInSlot(0, "Diamond")) { info("Diamond found in slot 0!") }`

- **`getOpenContainer(): String`**
  Returns the type of the currently open container.
  Example: `info("Open container: ${getOpenContainer()}")`

- **`click(slot: Int, button: Int)`**
  Clicks a slot in the currently open container.
  Example: `click(0, 0)` (Left-clicks the first slot)

- **`moveItem(sourceSlot: Int, destinationSlot: Int)`**
  Moves an item from one slot to another.
  Example: `moveItem(0, 1)` (Moves item from slot 0 to slot 1)

- **`moveItem(sourceSlot: Int, destinationSlot: Int, simulate: Boolean)`**
  Moves an item from one slot to another, optionally simulating the action.
  Example: `moveItem(0, 1, true)` (Simulates moving the item)

- **`dropStack(slot: Int)`**
  Drops the stack in the specified slot.
  Example: `dropStack(0)` (Drops the item in slot 0)

- **`getInventoryItems(): List<ItemStack>`**
  Returns a list of items in the player's inventory.
  Example: `getInventoryItems().forEach { info("Item: ${getItemName(it)}") }`

### Item Data
- **`getItemName(stack: ItemStack): String`**
  Returns the name of the item stack.
  Example: `info("Item name: ${getItemName(stack)}")`

- **`getItemCount(stack: ItemStack): Int`**
  Returns the count of items in the stack.
  Example: `info("Item count: ${getItemCount(stack)}")`

- **`getMaxItemCount(stack: ItemStack): Int`**
  Returns the maximum stack size for the item.
  Example: `info("Max stack size: ${getMaxItemCount(stack)}")`

- **`getItemDamage(stack: ItemStack): Int`**
  Returns the damage value of the item.
  Example: `info("Item damage: ${getItemDamage(stack)}")`

- **`getItemMaxDamage(stack: ItemStack): Int`**
  Returns the maximum damage value of the item.
  Example: `info("Max item damage: ${getItemMaxDamage(stack)}")`

### Utilities
- **`delay(duration: Long)`**
  Pauses the script for the specified duration (in milliseconds).
  Example: `delay(1000)` (Pauses for 1 second)

## Example Script

```kotlin
// Log a message
wbh.info("Starting script...")

// Send a chat message
wbh.chat("Hello, Minecraft!")

// Check if a diamond is in slot 0
if (wbh.isItemInSlot(0, "Diamond")) {
    wbh.feedback("Diamond found in slot 0!")
} else {
    wbh.feedback("No diamond in slot 0.")
}

// Move an item from slot 0 to slot 1
wbh.moveItem(0, 1)

// Pause for 2 seconds
wbh.delay(2000)

// Log the open container type
wbh.feedback("Open container: ${wbh.getOpenContainer()}")

// Display feedback in-game
wbh.feedback("Script executed successfully!")
```
