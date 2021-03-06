package org.trippi.impl.base;

import gnu.trove.TIntHashSet;

import java.util.Map;

import org.jrdf.graph.Node;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;

/**
 * Ensures no dupes while iterating through the wrapped iterator.
 *
 * @author cwilper@cs.cornell.edu
 */
public class DistinctTupleIterator extends TupleIterator {

    private TupleIterator m_wrapped;
    private TIntHashSet m_seen;
    private Map<String, Node> m_next;
    private boolean m_closed = false;

    public DistinctTupleIterator(TupleIterator wrapped) throws TrippiException {
        m_wrapped = wrapped;
        m_seen = new TIntHashSet();
        m_next = getNext();
    }

    // return null if there are no more
    private Map<String, Node> getNext() throws TrippiException {
        while (m_wrapped.hasNext()) {
            Map<String, Node> nextMap = m_wrapped.next();
            if (!seen(nextMap.hashCode())) return nextMap;
        }
        return null;
    }

    private boolean seen(int id) {
        if (m_seen.contains(id)) return true;
        m_seen.add(id);
        return false;
    }

    @Override
	public boolean hasNext() {
        return (m_next != null);
    }

    @Override
	public Map<String, Node> next() throws TrippiException {
        if (m_next == null) return null;
        Map<String, Node> last = m_next;
        m_next = getNext();
        return last;
    }

    @Override
	public String[] names() throws TrippiException {
        return m_wrapped.names();
    }

    @Override
	public void close() throws TrippiException {
        if (!m_closed) {
            m_wrapped.close();
            m_closed = true;
        }
    }

    @Override
	public void finalize() throws TrippiException {
        close();
    }

}
