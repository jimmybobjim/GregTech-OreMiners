package org.jimmybobjim.oreminers.common.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class LangHandler extends com.gregtechceu.gtceu.data.lang.LangHandler {
    public static void init(RegistrateLangProvider provider) {
        // Commands
        provider.add("command.gt_oreminers.vein_core.not_vein_core", "(%s) is not a vein core");
        provider.add("command.gt_oreminers.vein_core.set_purity.success", "Changed purity at (%s) from %s to %s");
        provider.add("command.gt_oreminers.vein_core.get_purity.success", "Purity at (%s) is %s");
        provider.add("command.gt_oreminers.vein_core.set_remaining.success", "Changed remaining capacity at (%s) from %s to %s");
        provider.add("command.gt_oreminers.vein_core.get_remaining.success", "Remaining capacity at (%s) is %s");

        provider.add("gt_oreminers.multiblock.vein_core_miner.tier_too_high", "Tier too high!");
        provider.add("gt_oreminers.multiblock.pattern.error.invalid_block", "Invalid block!");
        provider.add("gt_oreminers.block.vein_core.remaining", "Remaining.. %s%%");
        provider.add("gt_oreminers.block.vein_core.purity", "Purity.......... %s%%");
    }
}
