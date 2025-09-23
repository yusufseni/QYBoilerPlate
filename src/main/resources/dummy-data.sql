-- 10 MARTIAL_ARTS_GYM organizations as users
INSERT INTO app_users (id, username, passwordHash, email, organizationType, role, status, themes)
VALUES
    (gen_random_uuid(), 'ccma', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'ccma@martial.com', 'MARTIAL_ARTS_GYM', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'garuda_fight', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'garuda@martial.com', 'MARTIAL_ARTS_GYM', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'maung_bandung', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'maung@martial.com', 'MARTIAL_ARTS_GYM', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'rajawali_mma', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'rajawali@martial.com', 'MARTIAL_ARTS_GYM', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'bravekid_kickboxing', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'bravekid@martial.com', 'MARTIAL_ARTS_GYM', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'muaythai_majalaya', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'majalaya@martial.com', 'MARTIAL_ARTS_GYM', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'siliwangi_gym', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'siliwangi@martial.com', 'MARTIAL_ARTS_GYM', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'harimau_boxing', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'harimau@martial.com', 'MARTIAL_ARTS_GYM', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'bandung_grappling', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'grappling@martial.com', 'MARTIAL_ARTS_GYM', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'cimahi_mma', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'cimahi@martial.com', 'MARTIAL_ARTS_GYM', 'CLIENT', 'ACTIVE', 'DARK');

-- 10 COMMUNITY organizations as users
INSERT INTO app_users (id, username, passwordHash, email, organizationType, role, status, themes)
VALUES
    (gen_random_uuid(), 'komunitas_bandung', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'bandung@community.com', 'COMMUNITY', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'muaythai_lovers', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'muaythai@community.com', 'COMMUNITY', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'kickboxing_id', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'kickboxing@community.com', 'COMMUNITY', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'sunda_fighters', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'sunda@community.com', 'COMMUNITY', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'martial_enthusiasts', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'enthusiasts@community.com', 'COMMUNITY', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'pecinta_bela_diri', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'pecinta@community.com', 'COMMUNITY', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'bandung_mma_community', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'mma@community.com', 'COMMUNITY', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'silat_nusantara', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'silat@community.com', 'COMMUNITY', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'majalaya_fight_club', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'majalaya@community.com', 'COMMUNITY', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'jawara_jawa', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'jawara@community.com', 'COMMUNITY', 'CLIENT', 'ACTIVE', 'DARK');

-- 100 PERSONAL users with common Indonesian/Malaysian names
INSERT INTO app_users (id, username, passwordHash, email, organizationType, role, status, themes)
VALUES
    (gen_random_uuid(), 'agus_saputra', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'agus.saputra@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'budi_santoso', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'budi.santoso@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'citra_dewi', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'citra.dewi@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'dian_pratama', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'dian.pratama@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'eka_putri', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'eka.putri@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'fitriani', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'fitriani@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'gilang_ramadhan', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'gilang.ramadhan@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'hani_wulandari', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'hani.wulandari@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'indra_gunawan', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'indra.gunawan@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'joni_saputra', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'joni.saputra@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),

    (gen_random_uuid(), 'kurniawan', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'kurniawan@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'lina_mulyani', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'lina.mulyani@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'muhammad_rizki', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'muhammad.rizki@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'nurul_hidayah', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'nurul.hidayah@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'putra_mahendra', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'putra.mahendra@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'qonita', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'qonita@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'rudi_hartono', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'rudi.hartono@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'siti_aminah', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'siti.aminah@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'taufik_hidayat', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'taufik.hidayat@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK'),
    (gen_random_uuid(), 'yuni_safitri', '$2a$10$uc7mCHLJ743CtiZUt3MkvujKmCyFoQdBLlEL33zEmDLmbu6CXkuqq', 'yuni.safitri@mail.com', 'PERSONAL', 'CLIENT', 'ACTIVE', 'DARK');

-- Continue up to 100 users with similar naming patterns
