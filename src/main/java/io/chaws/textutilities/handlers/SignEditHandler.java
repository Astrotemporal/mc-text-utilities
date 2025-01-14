package io.chaws.textutilities.handlers;

import io.chaws.textutilities.TextUtilities;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import static io.chaws.textutilities.utils.PlayerUtils.*;

public class SignEditHandler {
	public static void initialize() {
		UseBlockCallback.EVENT.register(SignEditHandler::onUseSignBlock);
	}

	private static ActionResult onUseSignBlock(
		final PlayerEntity player,
		final World world,
		final Hand hand,
		final BlockHitResult hitResult
	) {
		if (world.isClient) {
			return ActionResult.PASS;
		}

		if (player.isSneaking()) {
			return ActionResult.PASS;
		}

		// TODO: Remove this since editing signs is now vanilla
		if (!TextUtilities.getConfig().signEditingEnabled) {
			return ActionResult.PASS;
		}

		var blockPos = hitResult.getBlockPos();
		var blockEntity = world.getBlockEntity(blockPos);

		if (!(blockEntity instanceof SignBlockEntity signBlock)) {
			return ActionResult.PASS;
		}

		// Dyes and Ink Sacs can be applied to signs directly when they are in the main hand
		if (isHolding(player, Hand.MAIN_HAND, Items.INK_SAC) ||
			isHolding(player, Hand.MAIN_HAND, Items.GLOW_INK_SAC) ||
			isHolding(player, Hand.MAIN_HAND, Items.HONEYCOMB) ||
			isHoldingDye(player, Hand.MAIN_HAND)
		) {
			return ActionResult.PASS;
		}

		if (signBlock.isWaxed()) {
			return ActionResult.PASS;
		}

		player.openEditSignScreen(signBlock, signBlock.isPlayerFacingFront(player));

		return ActionResult.SUCCESS;
	}
}
