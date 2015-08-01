package com.voxelmodpack.common.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;

/**
 * Xmlns for Xml Helper
 * 
 * @author Adam Mummery-Smith
 */
public class XmlNamespaceContext implements NamespaceContext {
    private HashMap<String, String> prefixes = new HashMap<String, String>();

    public void addPrefix(String prefix, String namespaceURI) {
        this.prefixes.put(prefix, namespaceURI);
    }

    public void clear() {
        this.prefixes.clear();
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        return this.prefixes.keySet().iterator();
    }

    @Override
    public String getPrefix(String namespaceURI) {
        for (Entry<String, String> prefix : this.prefixes.entrySet()) {
            if (prefix.getValue().equals(namespaceURI))
                return prefix.getKey();
        }

        return null;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return this.prefixes.get(prefix);
    }
}
