# Duels

Duels, previously referred to as invites, let players / parties start matches with other players / parties.

# Intro

Duels can exist in player vs player, party vs party, and player vs party variations. The first two make up the majority of duels. When sent via
/duel, a message is sent to the target of the duel (either a player or a party) informing them of the duel and offering clickable buttons to
accept the duel. Duels cannot be explicitly declined, only ignored. When mutual duels have been sent (ex A sends a duel invite to B, B then
sends A a duel invite for the same kit type), the duel invite is automatically accepted.

# Commands

The two commands used for duels are `/duel` and `/accept`

`/duel`:
/duel can be used to send a duel invitation to another player / party.

Reference: [1](https://github.com/FrozenOrb/PotPvP/blob/master/potpvp-lobby/src/main/java/net/frozenorb/potpvp/lobby/invite/InviteHandler.java#L304),
[2](https://github.com/FrozenOrb/PotPvP/blob/master/potpvp-lobby/src/main/java/net/frozenorb/potpvp/lobby/invite/InviteHandler.java#L170)

# Menus

Menus used to select kit types for duels (/duel) are the standard `SelectKitType` menu.

The only other menu present is the 'other parties' menu. This menu is shown to party leaders and displays all other parties on the server,
paginated and sorted by size. The menu contains one button per other party online, whose lore is as follows:
```
&e&l%leader%'s party

&b*%leader%
&a%member 1%
&a%member 2%
&a%member 3%
[ and so on ]

&a&lCLICK HERE &6to send a duel request.
```
When clicked a duel request is sent to the target party, in the same way `/duel <party leader>` works.

This menu should live update.

Reference: [1](https://github.com/FrozenOrb/PotPvP/blob/master/potpvp-lobby/src/main/java/net/frozenorb/potpvp/lobby/invite/button/otherparties/ChallengePartyButton.java),
[2](https://github.com/FrozenOrb/PotPvP/blob/master/potpvp-lobby/src/main/java/net/frozenorb/potpvp/lobby/invite/menu/OtherPartiesMenu.java)

# Technical Comments

* qLib's menu system has a built in pagination system, visible [here](https://github.com/FrozenOrb/qLib/tree/master/src/main/java/net/frozenorb/qlib/menu/pagination).
* 
