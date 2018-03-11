/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.handler;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.block.property.SolidSideProperty;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.event.RefreshAbilitiesPlayerEvent;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.message.handler.ContextInject;
import org.lanternpowered.server.network.message.handler.NetworkMessageHandler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerAbilities;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerMovement;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerMovementAndLook;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerMovementInput;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerOnGroundState;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSneak;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSprint;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerVehicleMovement;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInStartElytraFlying;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityVelocity;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class PlayProtocolMovementHandler {

    @ContextInject private NetworkSession session;

    @NetworkMessageHandler
    private void handleStartElytraFlying(MessagePlayInStartElytraFlying message) {
        this.session.getPlayer().handleStartElytraFlying();
    }

    @NetworkMessageHandler
    private void handlePlayerAbilities(MessagePlayInPlayerAbilities message) {
        final boolean flying = message.isFlying();
        final LanternPlayer player = this.session.getPlayer();
        if (!flying || player.get(Keys.CAN_FLY).orElse(false)) {
            player.offer(Keys.IS_FLYING, flying);
        } else {
            // TODO: Just set velocity once it's implemented
            if (player.get(LanternKeys.SUPER_STEVE).orElse(false)) {
                this.session.send(new MessagePlayOutEntityVelocity(player.getNetworkId(), 0, 1.0, 0));
                player.offer(LanternKeys.IS_ELYTRA_FLYING, true);
            } else if (player.get(LanternKeys.CAN_WALL_JUMP).orElse(false)) {
                final Location<World> location = player.getLocation();

                // Get the horizontal direction the player is looking
                final Direction direction = player.getHorizontalDirection(Direction.Division.CARDINAL);

                // Get the block location the player may step against
                final Location<World> location1 = location.add(direction.asOffset().mul(0.6, 0, 0.6));

                SolidSideProperty solidSideProperty = location1.getExtent().getProperty(
                        location1.getBlockPosition(), direction.getOpposite(), SolidSideProperty.class).orElse(null);
                // Make sure that the side you step against is solid
                //noinspection ConstantConditions
                if (solidSideProperty != null && solidSideProperty.getValue()) {
                    // Push the player a bit back in the other direction,
                    // to give a more realistic feeling when pushing off
                    // against a wall
                    final Vector3d pushBack = direction.asBlockOffset().toDouble().mul(-0.1);
                    // Push the player up
                    this.session.send(new MessagePlayOutEntityVelocity(player.getNetworkId(), pushBack.getX(), 0.8, pushBack.getZ()));
                } else {
                    // Now we try if the player can jump away from the wall

                    // Get the block location the player may step against
                    final Location<World> location2 = location.add(direction.asOffset().mul(-0.6, 0, -0.6));

                    solidSideProperty = location2.getExtent().getProperty(
                            location2.getBlockPosition(), direction, SolidSideProperty.class).orElse(null);

                    //noinspection ConstantConditions
                    if (solidSideProperty != null && solidSideProperty.getValue()) {
                        // Combine the vectors in the direction of the block face
                        // and the direction the player is looking
                        final Vector3d vector = direction.asBlockOffset().toDouble()
                                .mul(0.25).mul(1, 0, 1).add(0, 0.65, 0).add(player.getDirectionVector().mul(0.4, 0.25, 0.4));

                        // Push the player forward and up
                        this.session.send(new MessagePlayOutEntityVelocity(
                                player.getNetworkId(), vector.getX(), vector.getY(), vector.getZ()));
                    }
                }
            }
            player.triggerEvent(RefreshAbilitiesPlayerEvent.of());
        }
    }

    @NetworkMessageHandler
    public void handlePlayerSprint(MessagePlayInPlayerSprint message) {
        this.session.getPlayer().offer(Keys.IS_SPRINTING, message.isSprinting());
    }

    @NetworkMessageHandler
    private void handlePlayerSneak(MessagePlayInPlayerSneak message) {
        this.session.getPlayer().offer(Keys.IS_SNEAKING, message.isSneaking());
    }

    @NetworkMessageHandler
    private void handlePlayerMovementInput(MessagePlayInPlayerMovementInput message) {
        // TODO
    }

    @NetworkMessageHandler
    private void handlePlayerVehicleMovement(MessagePlayInPlayerVehicleMovement message) {
        // TODO
    }

    @NetworkMessageHandler
    private void handlePlayerMovementAndLook(MessagePlayInPlayerMovementAndLook message) {
        final LanternPlayer player = this.session.getPlayer();
        player.setRawPosition(new Vector3d(message.getX(), message.getY(), message.getZ()));
        player.setRawRotation(toRotation(message.getPitch(), message.getYaw()));
        player.handleOnGroundState(message.isOnGround());
    }

    @NetworkMessageHandler
    private void handlePlayerMovement(MessagePlayInPlayerMovement message) {
        final LanternPlayer player = this.session.getPlayer();
        player.setRawPosition(new Vector3d(message.getX(), message.getY(), message.getZ()));
        player.handleOnGroundState(message.isOnGround());
    }

    @NetworkMessageHandler
    private void handlePlayerLook(MessagePlayInPlayerLook message) {
        final LanternPlayer player = this.session.getPlayer();
        player.setRawRotation(toRotation(message.getPitch(), message.getYaw()));
        player.handleOnGroundState(message.isOnGround());
    }

    @NetworkMessageHandler
    private void handle(MessagePlayInPlayerOnGroundState message) {
        final LanternPlayer player = this.session.getPlayer();
        player.handleOnGroundState(message.isOnGround());
    }

    private static Vector3d toRotation(float yaw, float pitch) {
        while (yaw >= 360.0) {
            yaw -= 360.0;
        }
        while (yaw < 0.0) {
            yaw += 360.0;
        }
        while (pitch >= 360.0) {
            pitch -= 360.0;
        }
        while (pitch < 0.0) {
            pitch += 360.0;
        }
        return new Vector3d(yaw, pitch, 0);
    }
}
