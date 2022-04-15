/*
 *  Copyright (C) 2022 Lucas B. R. de Oliveira - IFSP/SCL
 *  Contact: lucas <dot> oliveira <at> ifsp <dot> edu <dot> br
 *
 *  This file is part of CTruco (Truco game for didactic purpose).
 *
 *  CTruco is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CTruco is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CTruco.  If not, see <https://www.gnu.org/licenses/>
 */

package com.bueno.domain.usecases.game;

import com.bueno.domain.entities.game.Game;
import com.bueno.domain.entities.intel.Intel;
import com.bueno.domain.usecases.bot.BotUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayWithBotsUseCaseTest {

    @Mock CreateGameUseCase createGameUseCase;
    @Mock FindGameUseCase findGameUseCase;
    @Mock BotUseCase botUseCase;
    @Mock Intel intel;
    @Mock Game game;
    @InjectMocks PlayWithBotsUseCase sut;

    @Test
    @DisplayName("Should throw if any injected use case is null")
    void shouldThrowIfAnyInjectedUseCaseIsNull() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new PlayWithBotsUseCase(null, findGameUseCase, botUseCase)),
                () -> assertThrows(NullPointerException.class,
                        () -> new PlayWithBotsUseCase(createGameUseCase, null, botUseCase)),
                () -> assertThrows(NullPointerException.class,
                        () -> new PlayWithBotsUseCase(createGameUseCase, findGameUseCase, null))
        );
    }

    @Test
    @DisplayName("Should throw if play with bots receive any null parameters")
    void shouldThrowIfPlayWithBotsReceiveAnyNullParameters() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> sut.playWithBots(new CreateForBotsRequestModel(null, "BotA", UUID.randomUUID(), "BotB"))),
                () -> assertThrows(NullPointerException.class,
                        () -> sut.playWithBots(new CreateForBotsRequestModel(UUID.randomUUID(), null, UUID.randomUUID(), "BotB"))),
                () -> assertThrows(NullPointerException.class,
                        () -> sut.playWithBots(new CreateForBotsRequestModel(UUID.randomUUID(), "BotA", null, "BotB"))),
                () -> assertThrows(NullPointerException.class,
                        () -> sut.playWithBots(new CreateForBotsRequestModel(UUID.randomUUID(), "BotA", UUID.randomUUID(), null)))
        );
    }

    @Test
    @DisplayName("Should play with bots if preconditions are met")
    void shouldPlayWithBotsIfPreconditionsAreMet() {
        final UUID uuidA = UUID.randomUUID();
        final UUID uuidB = UUID.randomUUID();
        final var requestModel = new CreateForBotsRequestModel(uuidA, "BotA", uuidB, "BotB");
        when(createGameUseCase.createForBots(any())).thenReturn(any());
        when(findGameUseCase.loadUserGame(uuidA)).thenReturn(Optional.of(game));
        when(botUseCase.playWhenNecessary(game)).thenReturn(intel);
        when(intel.gameWinner()).thenReturn(Optional.of(uuidA));
        assertNotNull(sut.playWithBots(requestModel));
    }

    @Test
    @DisplayName("Should Bot B be the winner if preconditions are met")
    void shouldBotBBeTheWinnerIfPreconditionsAreMet() {
        final UUID uuidA = UUID.randomUUID();
        final UUID uuidB = UUID.randomUUID();
        when(createGameUseCase.createForBots(any())).thenReturn(any());
        when(findGameUseCase.loadUserGame(uuidA)).thenReturn(Optional.of(game));
        when(botUseCase.playWhenNecessary(game)).thenReturn(intel);
        when(intel.gameWinner()).thenReturn(Optional.of(uuidB));
        final var requestModel = new CreateForBotsRequestModel(uuidA, "BotA", uuidB, "BotB");
        final var result = sut.playWithBots(requestModel);
        assertAll(
                () -> assertEquals("BotB", result.getName()),
                () -> assertEquals(uuidB, result.getUuid())
        );
    }
}