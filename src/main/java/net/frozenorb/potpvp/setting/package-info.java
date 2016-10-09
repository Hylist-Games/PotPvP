/**
 * Handles accessing, saving, updating, and presentation of player settings.
 *
 * This includes the /settings command, a settings menu, persistence, etc.
 * Clients using the settings API should only concern themselves with {@link net.frozenorb.potpvp.setting.SettingUpdateEvent},
 * {@link net.frozenorb.potpvp.setting.SettingHandler#getSetting(java.util.UUID, net.frozenorb.potpvp.setting.Setting)} and
 * {@link net.frozenorb.potpvp.setting.SettingHandler#updateSetting(java.util.UUID, net.frozenorb.potpvp.setting.Setting, boolean)},
 */
package net.frozenorb.potpvp.setting;