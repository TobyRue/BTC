#!/bin/bash
for file in $(find | grep -P '\.json|\.png\.mcmeta' | grep 'power_p'); do 
   new_file="$(echo "$file" | sed -e 's/power_p/ancient_power_p/')"
   echo Copying $file to $new_file
   cat "$file" | sed -e 's/power_p/ancient_power_p/' > "$new_file"
done
