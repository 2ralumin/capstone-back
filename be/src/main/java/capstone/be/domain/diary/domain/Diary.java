package capstone.be.domain.diary.domain;

import capstone.be.domain.hashtag.domain.Hashtag;
import capstone.be.global.entity.AuditingFields;

import io.hypersistence.utils.hibernate.type.json.JsonStringType;

import lombok.Getter;

import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;


@Transactional
@Getter
@ToString(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class Diary extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Long userId;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    private String weather;


    @JoinTable(
            name = "diary_hashtag",
            joinColumns = @JoinColumn(name = "diaryId"),
            inverseJoinColumns = @JoinColumn(name = "hashtagId")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    private Set<Hashtag> hashtags = new LinkedHashSet<>();

    @Setter
    private String font;

    @Setter
    private String mood;

    @Setter
    private String thumbnail;


    @Type(type="json")
    @Column(columnDefinition = "LONGTEXT")
    @Setter
    private List<BProperties> blocks;

    public Diary() {


    }


    private Diary(Long userId,String title, String weather, String mood, String font,String thumbnail, List<BProperties> blocks) {
        this.userId = userId;
        this.title = title;
        this.weather = weather;
        this.mood = mood;
        this.font=font;
        this.thumbnail=thumbnail;
        this.blocks =blocks;
    }

    public static Diary of(Long userId,String title, String weather, String mood, String font, List<BProperties> blocks){
        return new Diary(userId,title, weather, mood, font, null, blocks);
    }


    public static Diary of(Long userId,String title, String weather, String mood, String font,String thumbnail, List<BProperties> blocks){
        return new Diary(userId,title, weather, mood, font, thumbnail, blocks);
    }



    public void addHashtag(Hashtag hashtag) {
        this.getHashtags().add(hashtag);
    }

    public void addHashtags(Collection<Hashtag> hashtags) {
        this.getHashtags().addAll(hashtags);
    }

    public void clearHashtags() {
        this.getHashtags().clear();
    }
    /*
    private Diary(String title, String weather, String mood, String font,String thumbnail, List<BProperties> blocks) {
        this.title = title;
        this.weather = weather;
        this.mood = mood;
        this.font=font;
        this.thumbnail=thumbnail;
        this.blocks =blocks;
    }

    public static Diary of(String title, String weather, String mood, String font, List<BProperties> blocks){
        return new Diary(title, weather, mood, font, null, blocks);
    }


    public static Diary of(String title, String weather, String mood, String font,String thumbnail, List<BProperties> blocks){
        return new Diary(title, weather, mood, font, thumbnail, blocks);
    }
    */

}
