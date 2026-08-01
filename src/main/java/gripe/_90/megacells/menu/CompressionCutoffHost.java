package gripe._90.megacells.menu;

import appeng.menu.guisync.ClientActionKey;

public interface CompressionCutoffHost {
    ClientActionKey<Boolean> ACTION_SET_COMPRESSION_LIMIT = new ClientActionKey<>("openCompressionLimitMenu");

    void mega$nextCompressionLimit(boolean backwards);
}
