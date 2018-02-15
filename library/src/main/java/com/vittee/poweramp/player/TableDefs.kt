@file:Suppress("unused", "MayBeConstant", "MemberVisibilityCanBePrivate", "ObjectPropertyName")

package com.vittee.poweramp.player

import android.provider.BaseColumns


interface TableDefs {
    interface Files {
        companion object {
            val TABLE = "folder_files"
            val VIEW_MOST_PLAYED = "files_most_played"
            val VIEW_TOP_RATED = "files_most_played"
            val VIEW_RECENTLY_ADDED = "files_recently_added"
            val VIEW_RECENTLY_ADDED_FS = "files_recently_added_fs"
            val VIEW_RECENTLY_PLAYED = "files_recently_played"

            // Fields.

            val _ID = TABLE + "._id"

            /*
            * Short filename.
            * String.
            */
            val NAME = TABLE + ".name"

            /*
            * Track number (extracted from filename).
            * Int.
            */
            val TRACK_NUMBER = "track_number"

            /*
             * Track name without number.
             * String.
             */
            val NAME_WITHOUT_NUMBER = "name_without_number"

            /*
             * One of the TAG_* constants.
             * Int.
             */
            val TAG_STATUS = "tag_status"

            /*
             * Track # tag.
             * Int.
             */
            val TRACK_TAG = "track_tag"

            /*
             * Parent folder id.
             * Int.
             */
            val FOLDER_ID = "folder_id"

            /*
             * Title tag.
             * String.
             */
            val TITLE_TAG = TABLE + ".title_tag"

            /*
             * Duration in miliseconds.
             * Int.
             */
            val DURATION = "duration"

            /*
             * Int.
             */
            val UPDATED_AT = TABLE + ".updated_at"

            /*
             * One of the file types - see PowerAMPiAPI.Track.FileType class.
             */
            val FILE_TYPE = "file_type"

            /*
             * Int.
             */
            val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            val PLAYED_AT = TABLE + ".played_at"

            /*
             * Int.
             * This is file last modified time actually, for most filesystems
             */
            val FILE_CREATED_AT = "file_created_at"

            /*
             * Bitwise flag.
             * Int.
             */
            val AA_STATUS = "aa_status"

            /*
             * Full path. Works only if the query is joined with the folders.
             * String.
             */
            val FULL_PATH = Folders.PATH + "||" + NAME

            /*
             * Int.
             */
            val RATING = "rating"

            /*
             * Int.
             */
            val PLAYED_TIMES = TABLE + ".played_times"

            /*
             * Int.
             */
            val ALBUM_ID = TABLE + ".album_id"

            /*
             * Int.
             */
            val ARTIST_ID = TABLE + ".artist_id"

            /*
             * Int.
             */
            val COMPOSER_ID = TABLE + ".composer_id"

            /*
             * Int.
             */
            val YEAR = "year"

            /*
             * Int.
             */
            val OFFSET_MS = "offset_ms"

            /*
             * Int.
             */
            val CUE_FOLDER_ID = "cue_folder_id"

            /*
             * First seen time.
             * Int.
             */
            val CREATED_AT = TABLE + ".created_at"


            /**
             * tag_status
             */
            val TAG_NOT_SCANNED = 0
            /**
             * tag_status
             */
            val TAG_SCANNED = 1

            /**
             * aa_status
             */
            val AA_STATUS_NONE = 0
            /**
             * aa_status
             */
            val AA_STATUS_EMBED = 1
        }
    }


    interface Folders {
        companion object {
            val TABLE = "folders"

            // Fields.

            val _ID = TABLE + "._id"

            /*
             * volume_id (fatID) of the storage.
             * Int.
             */
            //public static final String VOLUME_ID = TABLE + ".volume_id";

            /*
             * Short name of the folder.
             * String.
             */
            val NAME = TABLE + ".name"

            /*
             * Short path of the parent folder.
             * String.
             */
            val PARENT_NAME = TABLE + ".parent_name"

            /*
             * Full path of the folder.
             * String.
             */
            val PATH = TABLE + ".path"

            /*
             * Folder album art/thumb image (short name).
             * String.
             */
            val THUMB = TABLE + ".thumb"

            /*
             * One of the THUMB_* constants.
             * Int.
             */
            val THUMB_STATUS = TABLE + ".thumb_status"

            /*
             * Number of files in a folder.
             * Int.
             */
            val NUM_FILES = TABLE + ".num_files"

            /*
             * Int.
             */
            val DIR_MODIFIED_AT = TABLE + ".dir_modified_at"

            /*
             * Int.
             */
            val UPDATED_AT = TABLE + ".updated_at"
            /*
             * Id of the parent folder or 0 for "root" folders.
             * Int.
             */
            val PARENT_ID = TABLE + ".parent_id"
            /*
             * Number of child subfolders.
             * Int.
             */
            val NUM_SUBFOLDERS = TABLE + ".num_subfolders"

            /*
             * Int.
             */
            val IS_CUE = TABLE + ".is_cue"

            /*
             * Set for real (non-cue-virtual) folders, means number of cue source files inside this folder.
             * Int.
             */
            val NUM_CUE_FILES = TABLE + ".num_cue_files"

            /*
             * Int.
             */
            val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            val PLAYED_AT = TABLE + ".played_at"

            /*
             *
             * First seen time.
             * Int.
             */
            val CREATED_AT = TABLE + ".created_at"
        }
    }


    interface Albums {
        companion object {
            val TABLE = "albums"

            // Fields.

            val _ID = TABLE + "._id"

            /*
             * String.
             */
            val ALBUM = "album"

            /*
             * String.
             */
            val ALBUM_SORT = "album_sort"

            /*
             * Int
             */
            //public static final String NUM_FILES = TABLE + ".num_files";
            /*
             * Int.
             */
            val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            val PLAYED_AT = TABLE + ".played_at"
            /*
             * First seen time.
             * Int.
             */
            val CREATED_AT = TABLE + ".created_at"

            // Albums uses special where for cue sources, thus just count files is enough.
            val COUNT_FILES = "count(folder_files._id)"
        }
        //		public static final String COUNT_FILES_RAW = "(SELECT COUNT(*) FROM folder_files WHERE album_id=albums._id)";
        //		public static final String COUNT_FILES_NON_CUE_SOURCE = "(SELECT COUNT(*) FROM folder_files WHERE album_id=albums._id AND folder_files.cue_folder_id IS NULL)";
        //		public static final String COUNT_FILES_NON_CUE_SOURCE = "count(folder_files._id)";
    }


    interface Artists {
        companion object {
            val TABLE = "artists"

            // Fields.

            val _ID = TABLE + "._id"

            /*
             * String.
             */
            val ARTIST = "artist"

            /*
             * String.
             */
            val ARTIST_SORT = "artist_sort"

            /*
             * Int.
             */
            val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            val PLAYED_AT = TABLE + ".played_at"
            /*
             * First seen time.
             * Int.
             */
            val CREATED_AT = TABLE + ".created_at"

            // Artists uses special where for cue sources, thus just count files is enough.
            val COUNT_FILES = "count(folder_files._id)"
        }
    }


    interface ArtistAlbums {
        companion object {
            val TABLE = "artist_albums"

            // Fields.

            val _ID = TABLE + "._id"

            /*
             * Int.
             */
            val ARTIST_ID = TABLE + ".artist_id"

            /*
             * Int.
             */
            val ALBUM_ID = TABLE + ".album_id"

            /*
             * Int
             */
            //public static final String NUM_FILES = TABLE + ".num_files";

            /*
             * Int.
             */
            val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            val PLAYED_AT = TABLE + ".played_at"
            /*
             * First seen time.
             * Int.
             */
            val CREATED_AT = TABLE + ".created_at"
        }
    }


    interface Composers {
        companion object {
            val TABLE = "composers"

            // Fields.

            val _ID = TABLE + "._id"

            /*
             * String.
             */
            val COMPOSER = "composer"

            val COMPOSER_SORT = "composer_sort"

            /*
             * Int.
             */
            val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            val PLAYED_AT = TABLE + ".played_at"
            /*
             * First seen time.
             * Int.
             */
            val CREATED_AT = TABLE + ".created_at"

            // Composers uses special where for cue sources, thus just count files is enough.
            val COUNT_FILES = "count(folder_files._id)"
        }
    }

    interface Genres {
        companion object {
            val TABLE = "genres"

            // Fields.

            val _ID = TABLE + "._id"

            /*
             * String.
             */
            val GENRE = "genre"

            /*
             * Int.
             */
            val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            val PLAYED_AT = TABLE + ".played_at"
            /*
             * First seen time.
             * Int.
             */
            val CREATED_AT = TABLE + ".created_at"

            val COUNT_FILES = "count(folder_files._id)"
        }
    }


    interface GenreEntries {
        companion object {
            val TABLE = "genre_entries"

            // Fields.

            val _ID = TABLE + "._id"

            /*
             * Actual id of the file in folder_files table.
             * Long.
             */
            val FOLDER_FILE_ID = "folder_file_id"

            /*
             * Gerne id.
             * Long.
             */
            val GENRE_ID = "genre_id"
        }
    }


    interface PlaylistEntries {
        companion object {
            val TABLE = "folder_playlist_entries"

            // Fields.

            val _ID = TABLE + "._id"

            /*
             * Actual id of the file in folder_files table.
             * Int.
             */
            val FOLDER_FILE_ID = "folder_file_id"

            /*
             * Folder Playlist id.
             * Int.
             */
            val PLAYLIST_ID = "playlist_id"

            /*
             * Sort order.
             * Int.
             */
            val SORT = "sort"
        }
    }


    interface Playlists {
        companion object {
            const val TABLE = "folder_playlists"

            // Fields.

            val _ID = TABLE + "._id"

            /*
             * Name of the playlist.
             * String.
             */
            val NAME = TABLE + ".name"

            /*
             * Int.
             */
            val MTIME = TABLE + ".mtime"

            /*
             * Int.
             */
            val PATH = TABLE + ".path"

            /*
             * Int.
             */
            val SSID = TABLE + ".ssid"
            /*
             * Int.
             */
            val PLAYED_AT = TABLE + ".played_at"
            /*
             * Int.
             */
            val CREATED_AT = TABLE + ".created_at"
            /*
    	 * Int.
    	 */
            val UPDATED_AT = TABLE + ".updated_at"

            val NUM_FILES_COUNT = "(SELECT COUNT(*) FROM " + PlaylistEntries.TABLE + " WHERE " + PlaylistEntries.PLAYLIST_ID + "=" + _ID + ") AS _track_count"
        }
    }


    object Queue {
        val TABLE = "queue"

        val _ID = TABLE + "._id"

        /*
		 * Folder file id.
		 * Int.
		 */
        val FOLDER_FILE_ID = TABLE + ".folder_file_id"

        /*
		 * Int.
		 */
        val CREATED_AT = TABLE + ".created_at"

        /*
		 * Int.
		 */
        val SORT = TABLE + ".sort"

        val CALC_PLAYED = "folder_files.played_at > queue.created_at"
        val CALC_UNPLAYED = "folder_files.played_at <= queue.created_at"
    }

    object ShuffleSessionIds {
        val TABLE = "shuffle_session_ids"

        val _ID = TABLE + "._id"
    }


    object EqPresets {
        val TABLE = "eq_presets"

        val _ID = TABLE + "._id"

        /*
	     * Predefined preset number (see res/values/arrays/eq_preset_labels) or NULL for custom preset.
	     * Int.
	     */
        val PRESET = "preset"

        /*
	     * Eq preset string.
	     * String.
	     */
        val _DATA = TABLE + "._data"

        /*
	     * Custom preset name or null for predefined preset.
	     * String.
	     */
        val NAME = TABLE + ".name"

        /*
	     * 1 if preset is bound to speaker, 0 otherwise.
	     * Int.
	     */
        val BIND_TO_SPEAKER = "bind_to_speaker"

        /*
	     * 1 if preset is bound to wired headset, 0 otherwise.
	     * Int.
	     */
        val BIND_TO_WIRED = "bind_to_wired"

        /*
	     * 1 if preset is bound to bluetooth audio output, 0 otherwise.
	     * Int.
	     */
        val BIND_TO_BT = "bind_to_bt"
    }


    class EqPresetSongs : BaseColumns {
        companion object {
            val TABLE = "eq_preset_songs"

            val _ID = TABLE + "._id"

            /*
		 * Either folder_file_id.
		 * Int.
		 */
            val FILE_ID = TABLE + ".file_id"

            /*
		 * Eq preset id.
		 * Int.
		 */
            val PRESET_ID = "preset_id"
        }
    }

    companion object {
        /**
         * Alias used for category. Useful when query is actually a multi table join.
         */
        val CATEGORY_ALIAS = "cat"

        val UNKNOWN_ID = 1000L

        val NUM_FILES_ALL = "num_files AS num_files_total"
        val NUM_FILES_NO_CUE = "(num_files - num_cue_files) AS num_files_total"


        /**
         * Alias used for category aliased table _id.
         */
        val CATEGORY_ALIAS_ID = CATEGORY_ALIAS + "._id"
    }
}