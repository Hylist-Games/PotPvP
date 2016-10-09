/**
 * Contains kit primitives used throughout the rest of the codebase,
 * most notably KitType and DetailedKitType.
 *
 * A distinction is made between a KitType and a DetailedKitType.
 * A KitType is something that players can edit kits for
 * (ex HCTeams, Soup, No Enchants, etc), a DetailedKitType is an
 * extension of a KitType which also includes a {@link net.frozenorb.potpvp.kittype.DebuffSetting}
 * stating if debuffs are allowed.
 *
 * This distinction is important because, for example, players
 * can queue for HCTeams with debuffs or HCTeams with no debuffs,
 * but players only only edit kits for HCTeams (without including if
 * debuffs are allowed or not)
 *
 * To some extent, a DetailedKitType can be considered a
 * Pair<KitType, DebuffSetting>
 */
package net.frozenorb.potpvp.kittype;