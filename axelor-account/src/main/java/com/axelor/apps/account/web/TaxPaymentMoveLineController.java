/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2019 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.account.web;

import com.axelor.apps.account.db.TaxPaymentMoveLine;
import com.axelor.apps.account.service.TaxPaymentMoveLineService;
import com.axelor.exception.service.TraceBackService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class TaxPaymentMoveLineController {

  public void computeTaxAmount(ActionRequest request, ActionResponse response) {
    try {
      TaxPaymentMoveLine taxPaymentMoveLine = request.getContext().asType(TaxPaymentMoveLine.class);
      taxPaymentMoveLine =
          Beans.get(TaxPaymentMoveLineService.class).computeTaxAmount(taxPaymentMoveLine);
      response.setValue("taxAmount", taxPaymentMoveLine.getTaxAmount());
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}
