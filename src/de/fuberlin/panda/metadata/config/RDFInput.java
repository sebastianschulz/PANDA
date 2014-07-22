package de.fuberlin.panda.metadata.config;

import java.util.HashMap;

/**
 * This class represents a static hashmap to match a {@code panda} RDF property
 * to a {@link MetadataType}.
 * 
 * @author Sebastian Schulz
 * @since 27.11.2013
 */
public class RDFInput {
	private static HashMap<String, MetadataType> predicates = new HashMap<>();
	
	public static void initPredicates() {
		predicates.put("Creation", MetadataType.OPERATIONAL);
		predicates.put("Access", MetadataType.OPERATIONAL);
		predicates.put("Deleting", MetadataType.OPERATIONAL);
		
		predicates.put("License", MetadataType.ADMINISTRATIVE_SCOPE);
		predicates.put("Rights", MetadataType.ADMINISTRATIVE_SCOPE);
		predicates.put("Duties", MetadataType.ADMINISTRATIVE_SCOPE);
		
		predicates.put("Latitude", MetadataType.AREAL_SCOPE);
		predicates.put("Longitude", MetadataType.AREAL_SCOPE);
		predicates.put("Location", MetadataType.AREAL_SCOPE);
		
		predicates.put("ExpirationDate", MetadataType.TEMPORAL_SCOPE);
		
		predicates.put("Format", MetadataType.TYPE);
		predicates.put("Language", MetadataType.TYPE);
	}
	
	public static MetadataType getMetadataType(String predicate) {
		return predicates.get(predicate);
	}
}
