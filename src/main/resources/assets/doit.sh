#!/bin/bash
for file in $(find | grep -P '\.json' | grep 'potion_pillar'); do 
   new_file="$(echo "$file" | sed -e 's/potion_pillar/ancient_potion_pillar/')"
   echo Copying $file to $new_file
   cat "$file" | sed -e 's/potion_pillar/ancient_potion_pillar/' > "$new_file"
done
