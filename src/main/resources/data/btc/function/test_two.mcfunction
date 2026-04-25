playsound minecraft:block.trial_spawner.spawn_item_begin master @a ~ ~ ~ 1 1.2

tellraw @a {"text": "Trial Core Activated Two!", "color": "gold", "bold": true}

particle minecraft:trial_spawner_detection_ominous ~ ~0.5 ~ 0.5 0.5 0.5 0.1 50

give @p[distance=..5] minecraft:netherite_ingot 1
loot insert ~ ~1 ~ loot btc:chests/better_trial_chambers/rare_barrel_loot
