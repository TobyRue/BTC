# Plays a sound at the block's location
playsound minecraft:block.trial_spawner.spawn_item_begin master @a ~ ~ ~ 1 1.2

# Sends a message to everyone (useful for debugging)
tellraw @a {"text": "Trial Core Activated!", "color": "gold", "bold": true}

# Spawns a ring of trial-style particles
particle minecraft:trial_spawner_detection ~ ~0.5 ~ 0.5 0.5 0.5 0.1 50

# Optional: Give a nearby player a small reward for testing
give @p[distance=..5] minecraft:glow_berries 1
loot insert ~ ~1 ~ loot btc:chests/better_trial_chambers/barrel_loot
