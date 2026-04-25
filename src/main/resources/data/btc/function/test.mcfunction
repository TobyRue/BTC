playsound minecraft:block.trial_spawner.spawn_item_begin master @a ~ ~ ~ 1 1.2

tellraw @a {"text": "Trial Core Activated!", "color": "gold", "bold": true}

particle minecraft:trial_spawner_detection ~ ~0.5 ~ 0.5 0.5 0.5 0.1 50

give @p[distance=..5] minecraft:glow_berries 1
loot insert ~ ~1 ~ loot btc:chests/better_trial_chambers/barrel_loot
