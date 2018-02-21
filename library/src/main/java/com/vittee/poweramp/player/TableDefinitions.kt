@file:Suppress("unused", "MayBeConstant", "MemberVisibilityCanBePrivate", "ObjectPropertyName")

package com.vittee.poweramp.player

import android.provider.BaseColumns


interface TableDefinitions {
    interface Files {
        companion object {
            const val TABLE = "folder_files"
            const val VIEW_MOST_PLAYED = "files_most_played"
            const val VIEW_TOP_RATED = "files_most_played"
            const val VIEW_RECENTLY_ADDED = "files_recently_added"
            const val VIEW_RECENTLY_ADDED_FS = "files_recently_added_fs"
            const val VIEW_RECENTLY_PLAYED = "files_recently_played"

            // Fields.

            const val _ID = TABLE + "._id"

            /*
            * Short filename.
            * String.
            */
            const val NAME = TABLE + ".name"

            /*
            * Track number (extracted from filename).
            * Int.
            */
            const val TRACK_NUMBER = "track_number"

            /*
             * Track name without number.
             * String.
             */
            const val NAME_WITHOUT_NUMBER = "name_without_number"

            /*
             * One of the TAG_* constants.
             * Int.
             */
            const val TAG_STATUS = "tag_status"

            /*
             * Track # tag.
             * Int.
             */
            const val TRACK_TAG = "track_tag"

            /*
             * Parent folder id.
             * Int.
             */
            const val FOLDER_ID = "folder_id"

            /*
             * Title tag.
             * String.
             */
            const val TITLE_TAG = TABLE + ".title_tag"

            /*
             * Duration in miliseconds.
             * Int.
             */
            const val DURATION = "duration"

            /*
             * Int.
             */
            const val UPDATED_AT = TABLE + ".updated_at"

            /*
             * One of the file types - see PowerAMPiAPI.Track.FileType class.
             */
            const val FILE_TYPE = "file_type"

            /*
             * Int.
             */
            const val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            const val PLAYED_AT = TABLE + ".played_at"

            /*
             * Int.
             * This is file last modified time actually, for most filesystems
             */
            const val FILE_CREATED_AT = "file_created_at"

            /*
             * Bitwise flag.
             * Int.
             */
            const val AA_STATUS = "aa_status"

            /*
             * Full path. Works only if the query is joined with the folders.
             * String.
             */
            const val FULL_PATH = Folders.PATH + "||" + NAME

            /*
             * Int.
             */
            const val RATING = "rating"

            /*
             * Int.
             */
            const val PLAYED_TIMES = TABLE + ".played_times"

            /*
             * Int.
             */
            const val ALBUM_ID = TABLE + ".album_id"

            /*
             * Int.
             */
            const val ARTIST_ID = TABLE + ".artist_id"

            /*
             * Int.
             */
            const val COMPOSER_ID = TABLE + ".composer_id"

            /*
             * Int.
             */
            const val YEAR = "year"

            /*
             * Int.
             */
            const val OFFSET_MS = "offset_ms"

            /*
             * Int.
             */
            const val CUE_FOLDER_ID = "cue_folder_id"

            /*
             * First seen time.
             * Int.
             */
            const val CREATED_AT = TABLE + ".created_at"


            /**
             * tag_status
             */
            const val TAG_NOT_SCANNED = 0
            /**
             * tag_status
             */
            const val TAG_SCANNED = 1

            /**
             * aa_status
             */
            const val AA_STATUS_NONE = 0
            /**
             * aa_status
             */
            const val AA_STATUS_EMBED = 1
        }
    }


    interface Folders {
        companion object {
            const val TABLE = "folders"

            // Fields.

            const val _ID = TABLE + "._id"

            /*
             * volume_id (fatID) of the storage.
             * Int.
             */
            //public static final String VOLUME_ID = TABLE + ".volume_id";

            /*
             * Short name of the folder.
             * String.
             */
            const val NAME = TABLE + ".name"

            /*
             * Short path of the parent folder.
             * String.
             */
            const val PARENT_NAME = TABLE + ".parent_name"

            /*
             * Full path of the folder.
             * String.
             */
            const val PATH = TABLE + ".path"

            /*
             * Folder album art/thumb image (short name).
             * String.
             */
            const val THUMB = TABLE + ".thumb"

            /*
             * One of the THUMB_* constants.
             * Int.
             */
            const val THUMB_STATUS = TABLE + ".thumb_status"

            /*
             * Number of files in a folder.
             * Int.
             */
            const val NUM_FILES = TABLE + ".num_files"

            /*
             * Int.
             */
            const val DIR_MODIFIED_AT = TABLE + ".dir_modified_at"

            /*
             * Int.
             */
            const val UPDATED_AT = TABLE + ".updated_at"
            /*
             * Id of the parent folder or 0 for "root" folders.
             * Int.
             */
            const val PARENT_ID = TABLE + ".parent_id"
            /*
             * Number of child subfolders.
             * Int.
             */
            const val NUM_SUBFOLDERS = TABLE + ".num_subfolders"

            /*
             * Int.
             */
            const val IS_CUE = TABLE + ".is_cue"

            /*
             * Set for real (non-cue-virtual) folders, means number of cue source files inside this folder.
             * Int.
             */
            const val NUM_CUE_FILES = TABLE + ".num_cue_files"

            /*
             * Int.
             */
            const val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            const val PLAYED_AT = TABLE + ".played_at"

            /*
             *
             * First seen time.
             * Int.
             */
            const val CREATED_AT = TABLE + ".created_at"
        }
    }


    interface Albums {
        companion object {
            const val TABLE = "albums"

            // Fields.

            const val _ID = TABLE + "._id"

            /*
             * String.
             */
            const val ALBUM = "album"

            /*
             * String.
             */
            const val ALBUM_SORT = "album_sort"

            /*
             * Int
             */
            //public static final String NUM_FILES = TABLE + ".num_files";
            /*
             * Int.
             */
            const val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            const val PLAYED_AT = TABLE + ".played_at"
            /*
             * First seen time.
             * Int.
             */
            const val CREATED_AT = TABLE + ".created_at"

            // Albums uses special where for cue sources, thus just count files is enough.
            const val COUNT_FILES = "count(folder_files._id)"
        }
        //		public static final String COUNT_FILES_RAW = "(SELECT COUNT(*) FROM folder_files WHERE album_id=albums._id)";
        //		public static final String COUNT_FILES_NON_CUE_SOURCE = "(SELECT COUNT(*) FROM folder_files WHERE album_id=albums._id AND folder_files.cue_folder_id IS NULL)";
        //		public static final String COUNT_FILES_NON_CUE_SOURCE = "count(folder_files._id)";
    }


    interface Artists {
        companion object {
            const val TABLE = "artists"

            // Fields.

            const val _ID = TABLE + "._id"

            /*
             * String.
             */
            const val ARTIST = "artist"

            /*
             * String.
             */
            const val ARTIST_SORT = "artist_sort"

            /*
             * Int.
             */
            const val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            const val PLAYED_AT = TABLE + ".played_at"
            /*
             * First seen time.
             * Int.
             */
            const val CREATED_AT = TABLE + ".created_at"

            // Artists uses special where for cue sources, thus just count files is enough.
            const val COUNT_FILES = "count(folder_files._id)"
        }
    }


    interface ArtistAlbums {
        companion object {
            const val TABLE = "artist_albums"

            // Fields.

            const val _ID = TABLE + "._id"

            /*
             * Int.
             */
            const val ARTIST_ID = TABLE + ".artist_id"

            /*
             * Int.
             */
            const val ALBUM_ID = TABLE + ".album_id"

            /*
             * Int
             */
            //public static final String NUM_FILES = TABLE + ".num_files";

            /*
             * Int.
             */
            const val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            const val PLAYED_AT = TABLE + ".played_at"
            /*
             * First seen time.
             * Int.
             */
            const val CREATED_AT = TABLE + ".created_at"
        }
    }


    interface Composers {
        companion object {
            const val TABLE = "composers"

            // Fields.

            const val _ID = TABLE + "._id"

            /*
             * String.
             */
            const val COMPOSER = "composer"

            const val COMPOSER_SORT = "composer_sort"

            /*
             * Int.
             */
            const val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            const val PLAYED_AT = TABLE + ".played_at"
            /*
             * First seen time.
             * Int.
             */
            const val CREATED_AT = TABLE + ".created_at"

            // Composers uses special where for cue sources, thus just count files is enough.
            const val COUNT_FILES = "count(folder_files._id)"
        }
    }

    interface Genres {
        companion object {
            const val TABLE = "genres"

            // Fields.

            const val _ID = TABLE + "._id"

            /*
             * String.
             */
            const val GENRE = "genre"

            /*
             * Int.
             */
            const val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            const val PLAYED_AT = TABLE + ".played_at"
            /*
             * First seen time.
             * Int.
             */
            const val CREATED_AT = TABLE + ".created_at"

            const val COUNT_FILES = "count(folder_files._id)"
        }
    }


    interface GenreEntries {
        companion object {
            const val TABLE = "genre_entries"

            // Fields.

            const val _ID = TABLE + "._id"

            /*
             * Actual id of the file in folder_files table.
             * Long.
             */
            const val FOLDER_FILE_ID = "folder_file_id"

            /*
             * Gerne id.
             * Long.
             */
            const val GENRE_ID = "genre_id"
        }
    }


    interface PlaylistEntries {
        companion object {
            const val TABLE = "folder_playlist_entries"

            // Fields.

            const val _ID = TABLE + "._id"

            /*
             * Actual id of the file in folder_files table.
             * Int.
             */
            const val FOLDER_FILE_ID = "folder_file_id"

            /*
             * Folder Playlist id.
             * Int.
             */
            const val PLAYLIST_ID = "playlist_id"

            /*
             * Sort order.
             * Int.
             */
            const val SORT = "sort"
        }
    }


    interface Playlists {
        companion object {
            const val TABLE = "folder_playlists"

            // Fields.

            const val _ID = TABLE + "._id"

            /*
             * Name of the playlist.
             * String.
             */
            const val NAME = TABLE + ".name"

            /*
             * Int.
             */
            const val MTIME = TABLE + ".mtime"

            /*
             * Int.
             */
            const val PATH = TABLE + ".path"

            /*
             * Int.
             */
            const val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            const val PLAYED_AT = TABLE + ".played_at"
            /*
             * Int.
             */
            const val CREATED_AT = TABLE + ".created_at"
            /*
    	 * Int.
    	 */
            const val UPDATED_AT = TABLE + ".updated_at"

            const val NUM_FILES_COUNT = "(SELECT COUNT(*) FROM " + PlaylistEntries.TABLE + " WHERE " + PlaylistEntries.PLAYLIST_ID + "=" + _ID + ") AS _track_count"
        }
    }


    object Queue {
        const val TABLE = "queue"

        const val _ID = TABLE + "._id"

        /*
		 * Folder file id.
		 * Int.
		 */
        const val FOLDER_FILE_ID = TABLE + ".folder_file_id"

        /*
		 * Int.
		 */
        const val CREATED_AT = TABLE + ".created_at"

        /*
		 * Int.
		 */
        const val SORT = TABLE + ".sort"

        const val CALC_PLAYED = "folder_files.played_at > queue.created_at"
        const val CALC_UNPLAYED = "folder_files.played_at <= queue.created_at"
    }

    object ShuffleSessionIds {
        const val TABLE = "shuffle_session_ids"

        const val _ID = TABLE + "._id"
    }


    object EqPresets {
        const val TABLE = "eq_presets"

        const val _ID = TABLE + "._id"

        /*
	     * Predefined preset number (see res/values/arrays/eq_preset_labels) or NULL for custom preset.
	     * Int.
	     */
        const val PRESET = "preset"

        /*
	     * Eq preset string.
	     * String.
	     */
        const val _DATA = TABLE + "._data"

        /*
	     * Custom preset name or null for predefined preset.
	     * String.
	     */
        const val NAME = TABLE + ".name"

        /*
	     * 1 if preset is bound to speaker, 0 otherwise.
	     * Int.
	     */
        const val BIND_TO_SPEAKER = "bind_to_speaker"

        /*
	     * 1 if preset is bound to wired headset, 0 otherwise.
	     * Int.
	     */
        const val BIND_TO_WIRED = "bind_to_wired"

        /*
	     * 1 if preset is bound to bluetooth audio output, 0 otherwise.
	     * Int.
	     */
        const val BIND_TO_BT = "bind_to_bt"
    }


    class EqPresetSongs : BaseColumns {
        companion object {
            const val TABLE = "eq_preset_songs"

            const val _ID = TABLE + "._id"

            /*
		 * Either folder_file_id.
		 * Int.
		 */
            const val FILE_ID = TABLE + ".file_id"

            /*
		 * Eq preset id.
		 * Int.
		 */
            const val PRESET_ID = "preset_id"
        }
    }

    companion object {
        /**
         * Alias used for category. Useful when query is actually a multi table join.
         */
        const val CATEGORY_ALIAS = "cat"

        const val UNKNOWN_ID = 1000L

        const val NUM_FILES_ALL = "num_files AS num_files_total"
        const val NUM_FILES_NO_CUE = "(num_files - num_cue_files) AS num_files_total"


        /**
         * Alias used for category aliased table _id.
         */
        const val CATEGORY_ALIAS_ID = CATEGORY_ALIAS + "._id"
    }
}