package it.cnr.iasi.saks.semrel.method.gnldsd;

import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.method.SemanticRelatednessStrategy;
import it.cnr.iasi.saks.semrel.method.ldsd.LDSD_direct_weighted;
import it.cnr.iasi.saks.semrel.method.ldsd.LDSD_indirect_weighted;

public abstract class GNLDSD_Abstract implements SemanticRelatednessStrategy {
	LDSD_direct_weighted ldsd_direct_weighted = new LDSD_direct_weighted(this.getKb());
	LDSD_indirect_weighted ldsd_indirect_weighted = new LDSD_indirect_weighted(this.getKb());

	private KnowledgeBase kb = null;
			
	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}
	
	public LDSD_direct_weighted getLdsd_direct_weighted() {
		return ldsd_direct_weighted;
	}

	public void setLdsd_direct_weighted(LDSD_direct_weighted ldsd_direct_weighted) {
		this.ldsd_direct_weighted = ldsd_direct_weighted;
	}

	public LDSD_indirect_weighted getLdsd_indirect_weighted() {
		return ldsd_indirect_weighted;
	}

	public void setLdsd_indirect_weighted(LDSD_indirect_weighted ldsd_indirect_weighted) {
		this.ldsd_indirect_weighted = ldsd_indirect_weighted;
	}

}
