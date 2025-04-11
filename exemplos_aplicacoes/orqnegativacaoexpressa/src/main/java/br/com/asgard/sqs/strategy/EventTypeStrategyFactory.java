package br.com.asgard.sqs.strategy;

import br.com.asgard.domain.enums.EventType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class EventTypeStrategyFactory {

    private final Map<EventType, EventTypeStrategy> strategies = new EnumMap<>(EventType.class);

    public EventTypeStrategyFactory(final List<EventTypeStrategy> strategies){
        if(CollectionUtils.isNotEmpty(strategies)){
            strategies.forEach(this::addStrategy);
        }
    }

    private void addStrategy(final EventTypeStrategy strategy){
        if(Objects.nonNull(strategy)){
            strategies.put(strategy.getStrategyType(), strategy);
        }
    }

    public EventTypeStrategy getStrategy(final EventType eventType){
        return strategies.getOrDefault(eventType, new UnrecognizedStrategyImpl());
    }
}
