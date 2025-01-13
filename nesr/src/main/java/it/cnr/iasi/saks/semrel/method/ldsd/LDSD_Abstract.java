package it.cnr.iasi.saks.semrel.method.ldsd;

import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.method.SemanticRelatednessStrategy;

public abstract class LDSD_Abstract implements SemanticRelatednessStrategy {
		
	KnowledgeBase kb = null;
		
	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}

}
