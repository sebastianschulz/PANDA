package de.fuberlin.panda.metadata.parsedMetadata;

import de.fuberlin.panda.metadata.descriptive.AdministrativeScope;
import de.fuberlin.panda.metadata.descriptive.ArealScope;
import de.fuberlin.panda.metadata.descriptive.TemporalScope;
import de.fuberlin.panda.metadata.descriptive.Type;
import de.fuberlin.panda.metadata.exceptions.MetadataNotFoundException;
import de.fuberlin.panda.metadata.exceptions.ProcessingUriException;
import de.fuberlin.panda.metadata.operational.Operations;


/**
 * This abstract class is the internal representation of the metadata 
 * parsed from a valid source. It holds the static fields of operations,
 * the different scope types as well as the type information.
 * 
 * @see #parseMetadata()
 * 
 * @since 05.09.2013
 * @author Sebastian Schulz
 *
 */
public abstract class ParsedMetadata {
	protected String uri = "";
	
	protected Operations operations;
	
	//descriptive metadata
	protected AdministrativeScope administrativeScope;
	protected ArealScope arealScope;
	protected TemporalScope temporalScope;
	protected Type type;
//	protected SemanticDescription semanticDescription;
	
	public ParsedMetadata() {
		operations = new Operations();
		administrativeScope = new AdministrativeScope();
		arealScope = new ArealScope();
		temporalScope = new TemporalScope();
		type = new Type();
		//semanticDescription = new SemanticDescription();
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return uri;
	}
	
	public Operations getOperations() {
		return operations;
	}
	
	public AdministrativeScope getAdministrativeScope() {
		return administrativeScope;
	}
	
	public ArealScope getArealScope() {
		return arealScope;
	}
	
	public TemporalScope getTemporalScope() {
		return temporalScope;
	}
	
	public Type getType() {
		return type;
	}
	
	/**
	 * Abstract method for parsing metadata into the systems data classes.
	 * 
	 * @throws Exception which can be all kind of exceptions (see 
	 * 		{@link XMLMetadata#parseMetadata()} for a detailed look at the different
	 * 		exceptions)
	 */
	public abstract void parseMetadata() throws MetadataNotFoundException, ProcessingUriException;
}
