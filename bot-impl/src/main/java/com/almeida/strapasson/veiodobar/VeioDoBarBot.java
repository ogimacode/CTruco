package com.almeida.strapasson.veiodobar;

import com.bueno.spi.model.*;
import com.bueno.spi.service.BotServiceProvider;

import java.util.List;
import java.util.stream.Collectors;

public final class VeioDoBarBot implements BotServiceProvider {
    @Override
    public boolean getMaoDeOnzeResponse(GameIntel intel) {
        return false;
    }

    @Override
    public boolean decideIfRaises(GameIntel intel) {
        return false;
    }

    @Override
    public CardToPlay chooseCard(GameIntel intel) {
        var cards = sortedCards(intel);
        TrucoCard vira = intel.getVira();

        if (hasCasalMaior(vira, cards))
            return CardToPlay.of(cards.get(0));

        var refCard = intel.getOpponentCard().orElse(cards.get(0));
        TrucoCard minCardToWin = null;

        if (cards.get(2).compareValueTo(refCard, vira) > 0)
            minCardToWin = cards.get(2);
        if (cards.get(1).compareValueTo(refCard, vira) > 0)
            minCardToWin = cards.get(1);
        if (cards.get(0).compareValueTo(refCard, vira) > 0)
            minCardToWin = cards.get(0);

        return minCardToWin == null ? CardToPlay.discard(cards.get(0)) : CardToPlay.of(minCardToWin);
    }

    private List<TrucoCard> sortedCards(GameIntel intel) {
        return intel.getCards()
                .stream()
                .sorted((current, other) -> current.compareValueTo(other, intel.getVira()))
                .toList();
    }

    private boolean hasCasalMaior(TrucoCard vira, List<TrucoCard> cards) {
        var manilhaRank = vira.getRank().next();
        return cards.contains(TrucoCard.of(manilhaRank, CardSuit.HEARTS)) &&
                cards.contains(TrucoCard.of(manilhaRank, CardSuit.CLUBS));
    }

    @Override
    public int getRaiseResponse(GameIntel intel) {
        var param = TrucoCard.of(CardRank.JACK, CardSuit.SPADES);
        var vira = intel.getVira();

        var cardsGrateThanJackOrHavaManilha = intel.getCards()
                .stream()
                .filter(card -> card.compareValueTo(param, intel.getVira()) >= 0)
                .toList();

        return cardsGrateThanJackOrHavaManilha.isEmpty() || countMainilha(intel) < 2 ? -1 : 0;
    }

    private int countMainilha(GameIntel intel){
        var vira = intel.getVira();
        var allCards = sortedCards(intel);
        var contadorManilha = 0;

        for (TrucoCard allCard : allCards) {
            if (allCard.isManilha(vira))
                contadorManilha++;
        }

        return contadorManilha;
    }
}
