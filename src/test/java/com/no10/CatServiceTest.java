package com.no10;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;


import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatServiceTest {

    @Mock
    private CatMapper catMapper;

    @InjectMocks
    private CatService catService;

    @Test
    public void 全てのねこの情報が取得できること() {
        List<Cat> cats = List.of(
                new Cat("Omochi", "female", 2),
                new Cat("Coa", "male", 3),
                new Cat("Gonchi", "male", 5));
        when(catMapper.findAll()).thenReturn(cats);
        List<Cat> actual = catService.findCat(null, null, null);
        assertThat(actual).isEqualTo(cats);
        verify(catMapper).findAll();
    }

    @Test
    public void 存在するねこの名前を検索した場合にねこの情報が取得されること() {
        // テスト用データのセットアップ
        List<Cat> cats = List.of(
                new Cat("Omochi", "female", 2));
        when(catMapper.findByName("Omochi")).thenReturn(cats);

        // テスト実行
        List<Cat> actual = catService.findCat("Omochi", null, null);

        // 結果の検証
        assertThat(actual).isEqualTo(cats);
    }

    @Test
    public void 存在しないねこの名前を検索した場合に404エラーが返されること() {
        String catNotFoundName = "Tama";
        String errorMessage = catNotFoundName + "という名前のねこは存在しません。";

        CatNotFoundException exception = assertThrows(CatNotFoundException.class, () -> {
            catService.findCat(catNotFoundName, null, null);
        }, "404 NOT_FOUND");

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        verify(catMapper).findByName(catNotFoundName);
    }

    @Test
    public void 存在しないねこの名前を検索した場合に例外処理されること() {
        String catNotFoundName = "Tama";
        when(catMapper.findByName(catNotFoundName)).thenReturn(emptyList());

        //テストメソッドの呼び出しと例外の確認
        CatNotFoundException thrown = assertThrows(CatNotFoundException.class, () ->
                catService.findCat(catNotFoundName, null, null));

        //例外メッセージ
        final String exceptedMessage = catNotFoundName + "という名前のねこは存在しません。";
        assertThat(thrown.getMessage()).isEqualTo(exceptedMessage);
    }


    @Test
    public void 存在するねこの性別を検索した場合にねこの情報が取得されること() {
        List<Cat> cats = List.of(new Cat("Omochi", "female", 2));
        when(catMapper.findBySex("female")).thenReturn(cats);
        List<Cat> actual = catService.findCat(null, "female", null);
        assertThat(actual).isEqualTo(cats);
        verify(catMapper).findBySex("female");
    }

    @Test
    public void 存在しないねこの性別を検索した場合に404エラーが返されること() {
        String catNotFoundSex = "famaleee";
        String errorMessage = "現在、性別が" + catNotFoundSex + "のねこはいません。";

        CatNotFoundException exception = assertThrows(CatNotFoundException.class, () -> {
            catService.findCat(null, catNotFoundSex, null);
        }, "404 NOT_FOUND");
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        verify(catMapper).findBySex(catNotFoundSex);
    }

    @Test
    public void 存在しないねこの性別を検索した場合に例外処理されること() {
        String catNotFoundSex = "femaleee";
        when(catMapper.findBySex(catNotFoundSex)).thenReturn(emptyList());

        CatNotFoundException thrown = assertThrows(CatNotFoundException.class, () ->
                catService.findCat(null, catNotFoundSex, null));

        final String exceptedMessage = "現在、性別が" + catNotFoundSex + "のねこはいません。";
        assertThat(thrown.getMessage()).isEqualTo(exceptedMessage);
    }

    @Test
    public void 存在するねこの年齢を検索した場合にねこの情報が取得されること() {
        List<Cat> cats = List.of(new Cat("Omochi", "female", 2));
        when(catMapper.findByAge(2)).thenReturn(cats);
        List<Cat> actual = catService.findCat(null, null, 2);
        assertThat(actual).isEqualTo(cats);
        verify(catMapper).findByAge(2);
    }

    @Test
    public void 存在しないねこの年齢を検索した場合に404エラーが返されること() {
        Integer catNotFoundAge = 99;
        String errorMessage = "現在、" + catNotFoundAge + "才のねこはいません。";

        CatNotFoundException exception = assertThrows(CatNotFoundException.class, () -> {
            catService.findCat(null, null, catNotFoundAge);
        }, "404 NOT_FOUND");
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        verify(catMapper).findByAge(catNotFoundAge);
    }

    @Test
    public void 存在しないねこの年齢を検索した場合に例外処理されること() {
        Integer catNotFoundAge = 99;
        when(catMapper.findByAge(catNotFoundAge)).thenReturn(emptyList());

        CatNotFoundException thrown = assertThrows(CatNotFoundException.class, () ->
                catService.findCat(null, null, catNotFoundAge));

        final String exceptedMessage = "現在、" + catNotFoundAge + "才のねこはいません。";
        assertThat(thrown.getMessage()).isEqualTo(exceptedMessage);
    }

    @Test
    public void 新しいねこの情報を登録できること() {
        Cat cat = new Cat("Tama", "female", 0);
        assertThat(catService.insertCat("Tama", "female", 0)).isEqualTo(cat);
        verify(catMapper).insert(cat);

    }

    @Test
    public void 指定したねこの更新ができること() {
        Cat cat = new Cat("Omochi", "female", 3);
        Cat newCat = new Cat("Omochi", "female", 4);
        given(catMapper.findByName(cat.getName())).willReturn(List.of(cat));
        catService.updateCat(cat.getName(), cat.getSex(), newCat.getAge());
        verify(catMapper).update(newCat);
    }

    @Test
    public void 更新時に該当する名前のねこがいないときCatNotFoundExceptionとなること() {
        Cat cat = new Cat("Omochi", "female", 3);
        Cat newCat = new Cat("Omochi", "female", 4);
        given(catMapper.findByName(cat.getName())).willReturn(List.of(cat));
        catService.updateCat(cat.getName(), cat.getSex(), newCat.getAge());
        assertThatThrownBy(() -> {
            CatService.findCat("unknown", null, null);
        }).isInstanceOf(CatNotFoundException.class);
    }

    @Test
    public void 指定したねこの情報を削除できること() {
        Cat cat = new Cat("Omochi", "female", 3);
        when(catMapper.findByName(cat.getName())).thenReturn(List.of(cat));
        catService.deleteCat("Omochi");
        verify(catMapper).delete("Omochi");
    }

    @Test
    public void 指定したねこの名前がないときCatNotFoundExceptionとなること() {
        Cat cat = new Cat("unknown", null, null);
        when(catMapper.findByName(cat.getName())).thenReturn(emptyList());
        assertThatThrownBy(() -> {
            CatService.findCat("unknown", null, null);
        }).isInstanceOf(CatNotFoundException.class);


    }
};

