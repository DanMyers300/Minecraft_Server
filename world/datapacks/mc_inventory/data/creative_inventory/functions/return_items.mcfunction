# summon a new item entity
summon item ~ ~ ~ {Item:{id:"minecraft:stone",Count:1b},Tags:["new_item"]}

# copy the info about the entity from the marker entity
data modify entity @e[tag=new_item,limit=1] Item set from entity @s data.Inventory[0]

# remove the item from the marker
data remove entity @s data.Inventory[0]

# remove 1 from the amount of items that we still need to process
scoreboard players remove #items temp 1

# remove item tag
tag @e[tag=new_item] remove new_item

# run the same function again if there are more items to process
execute if score #items temp matches 1.. run function creative_inventory:return_items
