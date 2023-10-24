package com.test.stockmarketapp.presentation.company_info

import com.test.stockmarketapp.domain.model.CompanyInfo
import com.test.stockmarketapp.domain.model.IntradayInfo

data class CompanyInfoState(
    val stockInfos: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
