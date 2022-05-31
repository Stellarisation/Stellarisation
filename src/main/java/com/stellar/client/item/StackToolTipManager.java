package com.stellar.client.item;

import com.stellar.Stellar;
import com.stellar.item.RadioActiveItem;
import com.stellar.register.ItemRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class StackToolTipManager implements ItemTooltipCallback {

    private static final String ITEM_PREFIX = "item." + Stellar.ID;
    private static final String BLOCK_PREFIX = "block." + Stellar.ID;
    public static Text empty = new LiteralText("").formatted(Formatting.BLACK);

    public static void setup(){
        ItemTooltipCallback.EVENT.register(new StackToolTipManager());
    }

    @Override
    public void getTooltip(ItemStack stack, TooltipContext context, List<Text> tooltipLines) {
        if (MinecraftClient.getInstance().player == null)
            return;
        if (!MinecraftClient.getInstance().isOnThread())
            return;

        String translationKey = stack.getItem().getTranslationKey(stack);
        if (translationKey.startsWith(ITEM_PREFIX) || translationKey.startsWith(BLOCK_PREFIX)){
            if(stack.getItem() instanceof RadioActiveItem){
                double sievert = ((RadioActiveItem) stack.getItem()).getSievert();
                tooltipLines.addAll(radioactive(sievert));
            }
        }
    }

    public List<Text> radioactive(double sievert){
        List<Text> tips = new ArrayList<>();
        //tips.add(empty);
        tips.add(new LiteralText(Formatting.GRAY + I18n.translate("stellar.tooltip.radioactive") + ": "));
        if(sievert < 0.000001 ){
            double microSievert = (double) Math.round(sievert * Math.pow(10, 6)*100.0)/100;
            tips.add(new LiteralText(Formatting.YELLOW + " ▒▒▒ Low " + microSievert + "μSv/t"));
            return tips;
        }
        if(sievert < 0.001 ){
            double milliSievert = (double) Math.round(sievert * Math.pow(10, 3)*100.0)/100;
            tips.add(new LiteralText(Formatting.GOLD + " █▒▒ Medium " + milliSievert + "mSv/t"));
            return tips;
        }
        if(sievert < 1 ){
            tips.add(new LiteralText(Formatting.RED + " ██▒ High " + sievert + "Sv/t"));
            return tips;
        }
        tips.add(new LiteralText(Formatting.DARK_RED + " ███ Insane " + sievert + "Sv/t"));
        return tips;
    }
}
