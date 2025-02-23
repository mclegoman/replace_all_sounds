# Replace All Sounds
Replaces sounds with other sounds!  
*By default, all sounds are replaced with a low battery sound!*

## Dependencies
- [Fabric Loader](https://fabricmc.net) or [Quilt Loader](https://quiltmc.org)
- [Fabric API](https://modrinth.com/mod/fabric-api) (Optional, only required if you want the low battery sound.)

## How to change a sound
You can replace sounds using a resource pack!  
*note: subtitles will not be changed!*  
Sound Replacement Entries are located at `<namespace>:replace_all_sounds/<path>.json`.  
Sound Replacement Entries have the following data structure:
- `input`: `<namespace>:<path>` This is the identifier of the sound to replace.
    - `<namespace>:all` replaces all sounds within that namespace.
    - `all:all` replaces all sounds.
- `output`: `<namespace>:<path>` This is the identifier of the replacement sound.
- `enabled`: `true/false` (Defaults to true) If set to false, if a sound replacement is registered for input, it will be removed.

##  
Licensed under LGPL-3.0-or-later.

**This mod is not affiliated with/endorsed by Mojang Studios, or Microsoft.**  
The Minecraft Logo, and name are property of Mojang Studios and fall under Minecraft EULA.