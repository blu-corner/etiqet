package com.neueda.etiqet.core.util;

import com.neueda.etiqet.core.message.cdr.Cdr;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class MapUtils {

    public static void addMessageToGroup(final Map<String, Deque<Cdr>> map, final String groupKey, final Cdr message) {
        map.merge(
            groupKey,
            new LinkedList<>(singleton(message)),
            (q1, q2) ->
                Stream.of(q1, q2)
                    .flatMap(Collection::stream)
                    .collect(toCollection(LinkedList::new))
        );
    }

    public static <T> void addToMappedList(final Map<String, List<T>> map, final String key, final T element) {
        map.merge(
            key,
            singletonList(element),
            (list1, list2) ->
                Stream.of(list1, list2)
                    .flatMap(Collection::stream)
                    .collect(toList()));
    }
}
