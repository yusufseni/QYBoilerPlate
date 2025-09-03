package com.yoesoff.plate.enums.location;

public enum IndonesiaProvinces {
    ACEH("Aceh"),
    SUMATERA_UTARA("Sumatera Utara"),
    SUMATERA_BARAT("Sumatera Barat"),
    RIAU("Riau"),
    JAMBI("Jambi"),
    SUMATERA_SELATAN("Sumatera Selatan"),
    BENGKULU("Bengkulu"),
    LAMPUNG("Lampung"),
    KEPULAUAN_BANGKA_BELITUNG("Kepulauan Bangka Belitung"),
    KEPULAUAN_RIAU("Kepulauan Riau"),
    DKI_JAKARTA("DKI Jakarta"),
    JAWA_BARAT("Jawa Barat"),
    JAWA_TENGAH("Jawa Tengah"),
    DI_YOGYAKARTA("DI Yogyakarta"),
    JAWA_TIMUR("Jawa Timur"),
    BANTEN("Banten"),
    BALI("Bali"),
    NUSA_TENGGARA_BARAT("Nusa Tenggara Barat"),
    NUSA_TENGGARA_TIMUR("Nusa Tenggara Timur"),
    KALIMANTAN_BARAT("Kalimantan Barat"),
    KALIMANTAN_TENGAH("Kalimantan Tengah"),
    KALIMANTAN_SELATAN("Kalimantan Selatan"),
    KALIMANTAN_TIMUR("Kalimantan Timur"),
    KALIMANTAN_UTARA("Kalimantan Utara"),
    SULAWESI_UTARA("Sulawesi Utara"),
    SULAWESI_TENGAH("Sulawesi Tengah"),
    SULAWESI_SELATAN("Sulawesi Selatan"),
    SULAWESI_TENGGARA("Sulawesi Tenggara"),
    GORONTALO("Gorontalo"),
    SULAWESI_BARAT("Sulawesi Barat"),
    MALUKU("Maluku"),
    MALUKU_UTARA("Maluku Utara"),
    PAPUA_BARAT("Papua Barat"),
    PAPUA("Papua"),
    PAPUA_SELATAN("Papua Selatan"),
    PAPUA_TENGAH("Papua Tengah"),
    PAPUA_PEGUNUNGAN("Papua Pegunungan"),
    PAPUA_BARAT_DAYA("Papua Barat Daya");

    private final String label;

    IndonesiaProvinces(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
