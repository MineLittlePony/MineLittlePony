package com.mumfrey.liteloader.interfaces;

import java.util.Deque;

/**
 * Deque interface which is FastIterable
 * 
 * @author Adam Mummery-Smith
 *
 * @param <T>
 */
public interface FastIterableDeque<T> extends FastIterable<T>, Deque<T>
{
}
