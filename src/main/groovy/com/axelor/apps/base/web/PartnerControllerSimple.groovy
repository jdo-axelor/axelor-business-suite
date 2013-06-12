package com.axelor.apps.base.web

import com.axelor.apps.AxelorSettings
import com.axelor.apps.account.db.Invoice
import com.axelor.apps.base.db.IAdministration;
import com.axelor.apps.base.db.Partner
import com.axelor.apps.base.service.administration.SequenceService;
import com.axelor.exception.AxelorException
import com.axelor.apps.tool.net.URLService
import com.axelor.exception.service.TraceBackService
import com.axelor.exception.db.IException;
import com.axelor.rpc.ActionRequest
import com.axelor.rpc.ActionResponse
import groovy.util.logging.Slf4j
import com.google.inject.Inject;

@Slf4j
class PartnerControllerSimple {
	
	@Inject
	SequenceService sequenceService;
	
	void setPartnerSequence(ActionRequest request, ActionResponse response) {
		Partner partner = request.context as Partner
		Map<String,String> values = new HashMap<String,String>();
		if(partner.partnerSeq ==  null){
			def ref = sequenceService.getSequence(IAdministration.PARTNER,false);
			if (ref == null || ref.isEmpty())  
				throw new AxelorException("Aucune séquence configurée pour les tiers",
								IException.CONFIGURATION_ERROR);
			else
				values.put("partnerSeq",ref);
		}
		response.setValues(values);
	}
	
	
	/**
	 * Fonction appeler par le bouton imprimer
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	def void showPartnerInfo(ActionRequest request, ActionResponse response) {

		Partner partner = request.context as Partner

		StringBuilder url = new StringBuilder()
		AxelorSettings axelorSettings = AxelorSettings.get()
		
		url.append("${axelorSettings.get('axelor.report.engine', '')}/frameset?__report=report/Partner.rptdesign&__format=pdf&PartnerId=${partner.id}&__locale=fr_FR${axelorSettings.get('axelor.report.engine.datasource')}")

		log.debug("URL : {}", url)
		
		String urlNotExist = URLService.notExist(url.toString())
		if (urlNotExist == null){
		
			log.debug("Impression des informations sur le partenaire ${partner.partnerSeq} : ${url.toString()}")
			
			response.view = [
				"title": "Partenaire ${partner.partnerSeq}",
				"resource": url,
				"viewType": "html"
			]
		
		}
		else {
			response.flash = urlNotExist
		}
	}
	
}
