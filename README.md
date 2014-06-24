Feudalism
=========

Feudalism plugin for Bukkit 1.7.9 (v1.5.1)

Feudalism is designed to operate as a standalone plugin and is effectively a server mod.
It is incompatible with many popular plugins including Essentials, Lockette, and WorldGuard.

Feudalism divides the Minecraft world into four quadrants, separated by four 'badlands' areas
between them along the X and Z axes. Each quadrant represents a kingdom, and has limited
access to farming actions such as planting and breeding. The central spawn area is similarly
divided and represents each kingdom's 'embassy' where only the ruler can build. Support for a
spawn chamber and punch sings to teleport out (and back in) are included.

Each kingdom has one ruler, defined by the operator. Other players can apply to a kingdom, and
the ruler can accept (or reject) applications. Players that are not kingdom members can only
build in the badlands areas, and are prohibited from all restricted farming actions. They can
still fish, collect apples, create mushroom stew, and kill wild animals. Members of a kingdom
can act anywhere within their kingdom except spawn, and have access to kingdom rights for
farming within kingdom lands. Members can rank up within the kingdom, and the highest rank
grants the power to process applications and manage player ranks (making it possible for rulers
to delegate the work of management).

Kingdoms also have relations, and the rulers can decide (realistically, or role-playing)
to become good friends that share land rights or bitter enemies that frequently attack and
grief each other. Each player also has notoriety levels with the other kingdoms, which can
enable PvP in some interesting situations. Players that wish to arbitrarily PvP can always fight
in the badlands.

A detailed help system is integrated and all system commands are preprocessed by the plugin.

Each kingdom's rights, the spawn size, and badlands width can be adjusted by the operator.
A number of other useful commands are available to ops, including item creation and enchantment
and a lore command. Items with lore are immune to explosion and some forms of damage (but
are still vulnerable to fire and lava - bukkit bug) and are considered 'artifacts' by the
game. When an artifact with a name is picked up from the ground by any player, an announcement
is made to all online players.

Several artifacts have been hard-coded into the game. It is up to the op to physically manifest
these items. Please note that ops cannot trigger the special uses of artifacts (but players can).

Players cannot break, build, or interact with blocks or use any feature of the plugin if they do
not have the "kingdoms.player" permission. Be sure to set this permission!

Players that will be rulers should also have the "kingdoms.ruler" permission to grant them access
to server moderation tools. This is also required.

Feudalism-specific op features can be granted to a player by way of the "kingdoms.admin"
permission, but you should never need to use this since ops have all permissions.

Players with the "kingdoms.donor" permission will have access to a few extra perks. Please note
that according to the Mojang EULA, server operators are not allowed to charge for these perks.
This is something that should be fixed on our end, i.e. this option should not exist.

