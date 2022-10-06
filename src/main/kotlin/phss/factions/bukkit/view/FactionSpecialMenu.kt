package phss.factions.bukkit.view

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.persistence.PersistentDataType
import phss.factions.PFactions
import phss.factions.config.providers.BossConfig
import phss.factions.config.providers.menus.FactionSpecialMenuConfig
import phss.factions.conversation.FactionConversation
import phss.factions.conversation.providers.FactionBossControlConversation
import phss.factions.data.domain.Faction
import phss.factions.faction.extensions.replaceFactionInfo
import phss.factions.faction.special.entity.traits.BossTrait
import phss.factions.bukkit.view.schema.ItemSchema
import phss.factions.utils.extensions.replace
import phss.factions.utils.extensions.replaceColor
import phss.factions.bukkit.view.menu.dsl.menu
import phss.factions.bukkit.view.menu.dsl.slot
import phss.factions.utils.ItemBuilder

fun Player.openFactionSelectTypeMenu(plugin: PFactions, faction: Faction) = with(plugin) {
    val settings = FactionSpecialMenuConfig(plugin.factionSpecialMenu.get).selectTypeMenu

    menu(settings.name, settings.rows, plugin) {
        for (ornament in settings.ornamentItems) slot(ornament.slot, ornament.buildItem())

        for (item in settings.items) {
            slot(item.slot, item.buildItem()).onClick {
                faction.type = item.key
                close(player, true)

                factionManager.saveFaction(faction)
                player.sendMessage("TODO: TYPE SELECTED")
            }
        }
    }.openToPlayer(this@openFactionSelectTypeMenu)
}

fun Player.openFactionBossMenu(plugin: PFactions, faction: Faction) = with(plugin) {
    val settings = FactionSpecialMenuConfig(plugin.factionSpecialMenu.get).bossMenu
    val bossSettings = BossConfig(plugin.settings.get, faction.type!!)

    if (!bossSettings.exists()) return

    menu(settings.name, settings.rows, plugin) {
        for (ornament in settings.ornamentItems) slot(ornament.slot, ornament.buildItem())

        val itemSchema = with(bossSettings.configuration.display) {
            ItemSchema(name.replaceFactionInfo(faction, plugin), lore.replaceFactionInfo(faction, plugin), id, data, skullOwner, glow, key = faction.type!!)
        }
        slot(settings.bossItemSlot, itemSchema.buildItem()).onClick {
            val egg = ItemBuilder(Material.EGG).setName(itemSchema.name).build()
            val itemMeta = egg!!.itemMeta!!
            itemMeta.persistentDataContainer.set(plugin.specialManager.bossNamespacedKey, PersistentDataType.INTEGER, faction.id)
            egg.itemMeta = itemMeta
            player.inventory.addItem(egg)

            close(player, true)
        }
    }.openToPlayer(this@openFactionBossMenu)
}

fun Player.openFactionBossControlMenu(plugin: PFactions, trait: BossTrait) {
    val config = plugin.factionSpecialMenu.get
    val settings = FactionSpecialMenuConfig(config).controlMenu

    menu(settings.name, settings.rows, plugin) {
        for (selected in settings.items) {
            val item = ItemSchema.createNewInstance(selected)
            when (item.key.lowercase()) {
                "remove" -> slot(item.slot, item.buildItem()).onClick {
                    trait.bossEntity.despawn()
                    close(this@openFactionBossControlMenu, true)
                }
                "attack" -> {
                    if (trait.forceAttack != null) item.lore = config.getStringList("ControlMenu.items.attack.loreAttacking").replaceColor().replace("{player}", trait.forceAttack!!.name)

                    slot(item.slot, item.buildItem()).onClick {
                        if (trait.forceAttack == null) {
                            close(this@openFactionBossControlMenu, true)
                            FactionConversation(plugin, player, FactionBossControlConversation(trait, true)).begin()
                        } else {
                            trait.forceAttack = null
                            player.sendMessage(plugin.messages.getMessage("control.attackRemoved"))
                            close(this@openFactionBossControlMenu, true)
                        }
                    }
                }
                "friend" -> {
                    val none = config.getString("ControlMenu.items.friend.none")!!.replaceColor()
                    if (item.lore.contains("{horizontal}")) {
                        var playersString = ""
                        val separator = config.getString("ControlMenu.items.friend.separator")!!.replaceColor()
                        val end = config.getString("ControlMenu.items.friend.end")!!.replaceColor()
                        val format = config.getString("ControlMenu.items.friend.horizontalFormat")!!.replaceColor()

                        if (trait.friend.isEmpty()) playersString = none
                        else for (friend in trait.friend) {
                            if (trait.friend.getOrNull(trait.friend.indexOf(friend) + 1) == null) {
                                if (playersString == "") playersString = playersString.replace("{horizontal}", format.replace("{player}", friend))
                                else playersString +=  " $end ${format.replace("{player}", friend)}"
                                break
                            }

                            playersString += if (playersString == "") format.replace("{player}", friend) else "$separator ${format.replace("{player}", friend)}"
                        }

                        item.lore = item.lore.replace("{horizontal}", playersString)
                    } else {
                        if (trait.friend.isEmpty()) item.lore = item.lore.replace("{vertical}", none)
                        else {
                            val newLore = ArrayList<String>()
                            val format = config.getString("ControlMenu.items.friend.verticalFormat")!!.replaceColor()
                            for (line in item.lore) {
                                if (line.contains("{vertical}")) {
                                    for (friend in trait.friend) {
                                        newLore.add(format.replace("{player}", friend))
                                    }
                                } else newLore.add(line)
                            }

                            item.lore = newLore
                        }
                    }

                    slot(item.slot, item.buildItem()).onClick {
                        close(this@openFactionBossControlMenu, true)
                        FactionConversation(plugin, player, FactionBossControlConversation(trait, false, click == ClickType.LEFT)).begin()
                    }
                }
            }
        }
    }.openToPlayer(this)
}