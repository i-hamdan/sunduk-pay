package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.CreateGlobalPotService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.CaseRequirementType;
import com.bxb.sunduk_pay.util.PotScope;
import com.bxb.sunduk_pay.util.PotStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;

import static com.bxb.sunduk_pay.util.CaseCategory.COMMUNITY;
import static com.bxb.sunduk_pay.util.GlobalPotRequestType.CREATE_POT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CreateGlobalPotServiceTest {

    @Mock
    private GlobalPotRepository repository;
    @Mock
    private GlobalPotMapper mapper;

    @InjectMocks
    private CreateGlobalPotService service;


    GlobalPotRequest request(){
        return GlobalPotRequest.builder()
                .globalPotRequestType(CREATE_POT)
                .adminUuid("1aa695f5-f675-4726-8890-799f5873be71")
                .caseTitle("Masjid construction")
                .caseCategory(COMMUNITY)
                .potScope(PotScope.PUBLIC)
                .caseRequirementType(CaseRequirementType.NORMAL)
//                .potStatus(PotStatus.PENDING_VERIFICATION)
                .description("Contribute to the construction of masjid kulsumbi, become a part of our community.")
                .beneficiaryName("Masjid kulsumbi")
                .relationToBeneficiary("member")
                .address("Jehangirabad")
                .city("Bhopal")
                .country("India")
                .goalAmount(500000d)
                .goalDate(LocalDate.parse("2026-03-01"))
                .build();
    }

    @Test
    public void createGlobalPotTest() throws IOException {
        GlobalPot globalPot = GlobalPot.builder()
                .globalPotId("global_pot").build();
        when(mapper.toEntity(any(GlobalPotRequest.class))).thenReturn(globalPot);
        GlobalPotResponse response = service.perform(request());
        verify(repository,times(1)).save(any());
        assertNotNull(response);
    }
}