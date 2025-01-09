package org.jimmybobjim.oreminers.common.data;

import com.tterrag.registrate.providers.ProviderType;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.common.data.lang.LangHandler;

// TODO theres something wrong with running datagen, I can't be bothered to fix it rn
public class GTOMDatagen {
    public static void init() {
        GTOreMiners.REGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::init);
    }
}
